package at.demo.heatpump.device

import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/devices/{deviceId}/measurements")
class MeasurementController(
    private val measurementService: MeasurementService,
) {
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    fun create(
        @PathVariable deviceId: Long,
        @RequestBody request: MeasurementRequest,
    ): Measurement =
        measurementService.create(deviceId, request)

    @GetMapping
    fun findLatest(
        @PathVariable deviceId: Long,
    ): List<Measurement> =
        measurementService.findLatest(deviceId)
}