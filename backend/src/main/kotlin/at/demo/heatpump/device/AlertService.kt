package at.demo.heatpump.device

import org.springframework.stereotype.Service
import java.time.Instant

data class DeviceAlert(
    val code: String,
    val message: String,
    val actualValue: Double,
    val threshold: Double,
    val unit: String,
)

data class DeviceAlertResponse(
    val deviceId: Long,
    val measurementId: Long?,
    val recordedAt: Instant?,
    val alerts: List<DeviceAlert>,
)

@Service
class AlertService(
    private val measurementService: MeasurementService,
) {
    fun findCurrent(deviceId: Long): DeviceAlertResponse {
        val latest = measurementService.findLatest(deviceId).firstOrNull()

        if (latest == null) {
            return DeviceAlertResponse(
                deviceId = deviceId,
                measurementId = null,
                recordedAt = null,
                alerts = emptyList(),
            )
        }

        val alerts = mutableListOf<DeviceAlert>()

        if (latest.temperatureCelsius > 60.0) {
            alerts.add(
                DeviceAlert(
                    code = "HIGH_TEMPERATURE",
                    message = "Temperature exceeds the demo threshold.",
                    actualValue = latest.temperatureCelsius,
                    threshold = 60.0,
                    unit = "C",
                ),
            )
        }

        if (latest.pressureBar < 1.0) {
            alerts.add(
                DeviceAlert(
                    code = "LOW_PRESSURE",
                    message = "Pressure is below the demo threshold.",
                    actualValue = latest.pressureBar,
                    threshold = 1.0,
                    unit = "bar",
                ),
            )
        }

        if (latest.pressureBar > 3.0) {
            alerts.add(
                DeviceAlert(
                    code = "HIGH_PRESSURE",
                    message = "Pressure exceeds the demo threshold.",
                    actualValue = latest.pressureBar,
                    threshold = 3.0,
                    unit = "bar",
                ),
            )
        }

        return DeviceAlertResponse(
            deviceId = deviceId,
            measurementId = latest.id,
            recordedAt = latest.recordedAt,
            alerts = alerts,
        )
    }
}