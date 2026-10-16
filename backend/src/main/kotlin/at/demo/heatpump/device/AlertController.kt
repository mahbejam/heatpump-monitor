package at.demo.heatpump.device

import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/devices/{deviceId}/alerts")
class AlertController(
    private val alertService: AlertService,
) {
    @GetMapping
    fun findCurrent(
        @PathVariable deviceId: Long,
    ): DeviceAlertResponse =
        alertService.findCurrent(deviceId)
}