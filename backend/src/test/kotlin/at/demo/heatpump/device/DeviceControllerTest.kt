package at.demo.heatpump.device

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@SpringBootTest
@AutoConfigureMockMvc
class DeviceControllerTest(@Autowired private val mockMvc: MockMvc) {

    @Test
    fun `GET api devices returns all devices`() {
        mockMvc.get("/api/devices").andExpect {
            status { isOk() }
            jsonPath("$.length()") { value(4) }
            jsonPath("$[0].name") { value("Heatpump A") }
        }
    }

    @Test
    fun `GET api devices by id returns one device`() {
        mockMvc.get("/api/devices/2").andExpect {
            status { isOk() }
            jsonPath("$.location") { value("Kufstein") }
            jsonPath("$.status") { value("ONLINE") }
        }
    }

    @Test
    fun `GET api devices with unknown id returns 404`() {
        mockMvc.get("/api/devices/999").andExpect {
            status { isNotFound() }
        }
    }

    @Test
    fun `health endpoint is up`() {
        mockMvc.get("/actuator/health").andExpect {
            status { isOk() }
            jsonPath("$.status") { value("UP") }
        }
    }
}
