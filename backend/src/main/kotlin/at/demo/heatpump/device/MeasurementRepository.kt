package at.demo.heatpump.device

import org.springframework.data.jpa.repository.JpaRepository

interface MeasurementRepository : JpaRepository<Measurement, Long> {

    fun findTop100ByDeviceIdOrderByRecordedAtDescIdDesc(
        deviceId: Long,
    ): List<Measurement>
}