package at.demo.heatpump.device

data class MeasurementRequest(
    val temperatureCelsius: Double? = null,
    val pressureBar: Double? = null,
)