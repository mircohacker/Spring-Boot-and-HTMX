package org.example.ssrdemo

import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.http.HttpStatusCode
import org.springframework.web.ErrorResponse
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.servlet.ModelAndView


@ControllerAdvice
class MyErrorController {

    @ExceptionHandler(Exception::class)
    fun handleExceptions(
        e: Exception,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ModelAndView {
        val model: MutableMap<String, Any?> = mutableMapOf(
            "message" to e.message,
            "exception" to mapOf(
                "message" to e.message,
                "stacktrace" to e.stackTraceToString()
            )
        )
        val statusCode: HttpStatusCode = (if (e is ErrorResponse) e.statusCode else HttpStatus.INTERNAL_SERVER_ERROR)

        model["message"] = "Request to ${request.requestURI} failed with Code $statusCode"

        if (request.getHeader("HX-Request") == "true") {
            response.addHeader("HX-Reswap", "beforeend")
            response.addHeader("HX-Push-Url", "false")
            return ModelAndView("fragments/error", model, statusCode)
        }
        return ModelAndView("sites/error", model, statusCode)
    }
}