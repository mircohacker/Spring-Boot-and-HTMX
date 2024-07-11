package org.example.ssrdemo

import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class IntentionalErrorController {
    @GetMapping("triggerError")
    fun triggerError(): ModelAndView {
        return ModelAndView("sites/my-error", HttpStatus.BAD_REQUEST)
    }

    @GetMapping("exception")
    fun triggerException(): ModelAndView {
        throw NotImplementedError("This is not implemented yet")
    }

    @GetMapping("triggerError", headers = ["HX-Request=true"])
    fun triggerErrorHtmx(response: HttpServletResponse): ModelAndView {
        Thread.sleep(500)
        response.addHeader("HX-Reswap", "beforeend")
        response.addHeader("HX-Push-Url", "false")
        return ModelAndView("fragments/my-error", HttpStatus.BAD_REQUEST)
    }
}