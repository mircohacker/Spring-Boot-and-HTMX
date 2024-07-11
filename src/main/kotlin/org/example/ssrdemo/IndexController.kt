package org.example.ssrdemo

import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.servlet.ModelAndView

@Controller
class IndexController {
    @GetMapping("/", headers = ["HX-Request=true"])
    fun getIndexHtmx():ModelAndView{
        return ModelAndView("fragments/index")
    }
}