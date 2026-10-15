package at.demo.heatpump.device

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.GeneratedValue
import jakarta.persistence.GenerationType
import jakarta.persistence.Id
import jakarta.persistence.Table
import java.time.Instant

@Entity
@Table(name = "measurements")
open class Measurement(
    @field:Id
    @field:GeneratedValue(strategy = GenerationType.IDENTITY)
    open var id: Long? = null,

    @field:Column(nullable = false)
    open var deviceId: Long = 0,

    @field:Column(nullable = false)
    open var temperatureCelsius: Double = 0.0,

    @field:Column(nullable = false)
    open var pressureBar: Double = 0.0,

    @field:Column(nullable = false)
    open var recordedAt: Instant = Instant.now(),
)