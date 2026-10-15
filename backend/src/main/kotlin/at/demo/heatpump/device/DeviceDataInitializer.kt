package at.demo.heatpump.device

import org.springframework.boot.ApplicationArguments
import org.springframework.boot.ApplicationRunner
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional

@Component
class DeviceDataInitializer(
    private val deviceRepository: DeviceRepository,
) : ApplicationRunner {

    @Transactional
    override fun run(args: ApplicationArguments) {
        val devices = listOf(
            Device(1, "Heatpump A", "Wörgl", DeviceStatus.ONLINE),
            Device(2, "Heatpump B", "Kufstein", DeviceStatus.ONLINE),
            Device(3, "Heatpump C", "Innsbruck", DeviceStatus.OFFLINE),
            Device(4, "Heatpump D", "Salzburg", DeviceStatus.FAULT),
        )

        devices.forEach { device ->
            if (!deviceRepository.existsById(device.id)) {
                deviceRepository.save(device)
            }
        }
    }
}