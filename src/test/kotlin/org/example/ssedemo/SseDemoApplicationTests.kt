package org.example.ssedemo

import org.example.ssrdemo.SseDemoApplication
import org.hamcrest.core.IsNot
import org.hamcrest.core.StringContains
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.get

@AutoConfigureMockMvc
@SpringBootTest(classes = [SseDemoApplication::class])
class SseDemoApplicationTests {

    @Autowired
    lateinit var mockMvc: MockMvc

    @Test
    fun contextLoads() {
    }

    @Test
    fun indexIsServed() {
        mockMvc.get("/")
            .andExpect {
                content { string(StringContains("<!doctype html>")) }
                status { isOk() }
            }
    }

    @Test
    fun indexContentIsServedWithHtmx() {
        mockMvc.get("/")
        {
            headers {
                set("HX-Request", "true")
            }
        }.andExpect {
            content { string(IsNot(StringContains("<!doctype html>"))) }
            status { isOk() }
        }
    }

    @Test
    fun webjarsAreWorking() {
        mockMvc.get("/webjars/bootstrap/js/bootstrap.bundle.min.js").andExpect { status { isOk() } }
    }

}
