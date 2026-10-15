package at.demo.heatpump.device

import org.springframework.data.domain.Sort
import org.springframework.stereotype.Service

@Service
class DeviceService(
    private val deviceRepository: DeviceRepository,
) {
    fun findAll(): List<Device> =
        deviceRepository.findAll(Sort.by("id"))

    fun findById(id: Long): Device? =
        deviceRepository.findById(id).orElse(null)
}