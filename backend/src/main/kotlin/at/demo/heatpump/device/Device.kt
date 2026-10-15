package at.demo.heatpump.device

import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Id
import jakarta.persistence.Table

@Entity
@Table(name = "devices")
open class Device(
    @field:Id
    open var id: Long = 0,

    @field:Column(nullable = false)
    open var name: String = "",

    @field:Column(nullable = false)
    open var location: String = "",

    @field:Enumerated(EnumType.STRING)
    @field:Column(nullable = false)
    open var status: DeviceStatus = DeviceStatus.OFFLINE,
)

enum class DeviceStatus {
    ONLINE,
    OFFLINE,
    FAULT,
}