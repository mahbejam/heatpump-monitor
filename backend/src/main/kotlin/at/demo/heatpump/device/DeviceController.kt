package at.demo.heatpump.device

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import org.springframework.web.server.ResponseStatusException

@RestController
@RequestMapping("/api/devices")
class DeviceController(private val deviceService: DeviceService) {

    @GetMapping
    fun getAll(): List<Device> = deviceService.findAll()

    @GetMapping("/{id}")
    fun getById(@PathVariable id: Long): Device =
        deviceService.findById(id)
            ?: throw ResponseStatusException(HttpStatus.NOT_FOUND, "Device $id not found")
}
