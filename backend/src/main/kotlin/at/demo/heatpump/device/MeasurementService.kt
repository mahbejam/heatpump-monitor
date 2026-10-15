package at.demo.heatpump.device

import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import org.springframework.web.server.ResponseStatusException

@Service
class MeasurementService(
    private val measurementRepository: MeasurementRepository,
    private val deviceRepository: DeviceRepository,
) {
    @Transactional
    fun create(
        deviceId: Long,
        request: MeasurementRequest,
    ): Measurement {
        checkDeviceExists(deviceId)

        val temperature = request.temperatureCelsius
            ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "temperatureCelsius is required",
            )

        val pressure = request.pressureBar
            ?: throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "pressureBar is required",
            )

        if (!temperature.isFinite()) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "temperatureCelsius must be finite",
            )
        }

        if (!pressure.isFinite() || pressure < 0.0) {
            throw ResponseStatusException(
                HttpStatus.BAD_REQUEST,
                "pressureBar must be finite and non-negative",
            )
        }

        return measurementRepository.save(
            Measurement(
                deviceId = deviceId,
                temperatureCelsius = temperature,
                pressureBar = pressure,
            ),
        )
    }

    @Transactional(readOnly = true)
    fun findLatest(deviceId: Long): List<Measurement> {
        checkDeviceExists(deviceId)

        return measurementRepository
            .findTop100ByDeviceIdOrderByRecordedAtDescIdDesc(deviceId)
    }

    private fun checkDeviceExists(deviceId: Long) {
        if (!deviceRepository.existsById(deviceId)) {
            throw ResponseStatusException(
                HttpStatus.NOT_FOUND,
                "Device $deviceId not found",
            )
        }
    }
}