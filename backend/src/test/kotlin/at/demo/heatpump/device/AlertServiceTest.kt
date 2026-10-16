package at.demo.heatpump.device

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertNull
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Assertions.assertTrue
import org.junit.jupiter.api.Test
import org.mockito.Mockito
import org.springframework.http.HttpStatus
import org.springframework.web.server.ResponseStatusException
import java.time.Instant

class AlertServiceTest {
    private val measurementService =
        Mockito.mock(MeasurementService::class.java)

    private val alertService = AlertService(measurementService)

    private val recordedAt = Instant.parse("2026-10-15T12:00:00Z")

    private fun measurement(
        temperature: Double = 45.0,
        pressure: Double = 2.0,
        id: Long = 10L,
    ) = Measurement(
        id = id,
        deviceId = 1L,
        temperatureCelsius = temperature,
        pressureBar = pressure,
        recordedAt = recordedAt,
    )

    private fun givenMeasurements(vararg values: Measurement) {
        Mockito.`when`(measurementService.findLatest(1L))
            .thenReturn(values.toList())
    }

    @Test
    fun `normal measurement has no alerts and retains its timestamp`() {
        givenMeasurements(measurement())

        val result = alertService.findCurrent(1L)

        assertEquals(1L, result.deviceId)
        assertEquals(10L, result.measurementId)
        assertEquals(recordedAt, result.recordedAt)
        assertTrue(result.alerts.isEmpty())
    }

    @Test
    fun `missing measurement is distinguishable from normal measurement`() {
        givenMeasurements()

        val result = alertService.findCurrent(1L)

        assertEquals(1L, result.deviceId)
        assertNull(result.measurementId)
        assertNull(result.recordedAt)
        assertTrue(result.alerts.isEmpty())
    }

    @Test
    fun `temperature above threshold produces a high temperature alert`() {
        givenMeasurements(measurement(temperature = 60.1))

        val alert = alertService.findCurrent(1L).alerts.single()

        assertEquals("HIGH_TEMPERATURE", alert.code)
        assertEquals(60.1, alert.actualValue)
        assertEquals(60.0, alert.threshold)
        assertEquals("C", alert.unit)
    }

    @Test
    fun `pressure below threshold produces a low pressure alert`() {
        givenMeasurements(measurement(pressure = 0.9))

        val alert = alertService.findCurrent(1L).alerts.single()

        assertEquals("LOW_PRESSURE", alert.code)
        assertEquals(0.9, alert.actualValue)
        assertEquals(1.0, alert.threshold)
        assertEquals("bar", alert.unit)
    }

    @Test
    fun `pressure above threshold produces a high pressure alert`() {
        givenMeasurements(measurement(pressure = 3.1))

        val alert = alertService.findCurrent(1L).alerts.single()

        assertEquals("HIGH_PRESSURE", alert.code)
        assertEquals(3.1, alert.actualValue)
        assertEquals(3.0, alert.threshold)
    }

    @Test
    fun `values exactly at thresholds do not trigger alerts`() {
        for (pressure in listOf(1.0, 3.0)) {
            givenMeasurements(
                measurement(temperature = 60.0, pressure = pressure),
            )

            assertTrue(alertService.findCurrent(1L).alerts.isEmpty())
        }
    }

    @Test
    fun `temperature and pressure alerts can occur together`() {
        givenMeasurements(
            measurement(temperature = 70.0, pressure = 3.5),
        )

        val codes = alertService.findCurrent(1L).alerts
            .map { it.code }
            .toSet()

        assertEquals(
            setOf("HIGH_TEMPERATURE", "HIGH_PRESSURE"),
            codes,
        )
    }

    @Test
    fun `older abnormal measurements do not trigger current alerts`() {
        givenMeasurements(
            measurement(id = 11L),
            measurement(temperature = 80.0, pressure = 4.0, id = 10L),
        )

        val result = alertService.findCurrent(1L)

        assertEquals(11L, result.measurementId)
        assertTrue(result.alerts.isEmpty())
    }

    @Test
    fun `unknown device retains the not found response`() {
        Mockito.`when`(measurementService.findLatest(999L))
            .thenThrow(
                ResponseStatusException(
                    HttpStatus.NOT_FOUND,
                    "Device 999 not found",
                ),
            )

        val exception = assertThrows(
            ResponseStatusException::class.java,
        ) {
            alertService.findCurrent(999L)
        }

        assertEquals(HttpStatus.NOT_FOUND, exception.statusCode)
    }
}