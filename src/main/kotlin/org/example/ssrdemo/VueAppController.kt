package org.example.ssrdemo

import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.core.convert.converter.Converter
import org.springframework.stereotype.Component
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.servlet.ModelAndView
import java.io.IOException

@Controller()
@RequestMapping("/vue-app")
class VueAppController {

    @GetMapping()
    fun displayApp(): ModelAndView {
        return ModelAndView("sites/vue-app")
    }

    @GetMapping(headers = ["HX-Request=true"])
    fun displayAppHtmx(): ModelAndView {
        return ModelAndView("fragments/vue-app")
    }
}