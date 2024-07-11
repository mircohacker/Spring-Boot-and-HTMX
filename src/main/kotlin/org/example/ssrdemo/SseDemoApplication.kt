package org.example.ssrdemo

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Configuration


@SpringBootApplication
@Configuration
class SseDemoApplication

fun main(args: Array<String>) {
    runApplication<SseDemoApplication>(*args)
}