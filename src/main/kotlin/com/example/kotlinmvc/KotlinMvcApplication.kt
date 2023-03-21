package com.example.kotlinmvc

import io.micrometer.core.instrument.Counter
import io.micrometer.core.instrument.MeterRegistry
import io.micrometer.observation.ObservationRegistry
import io.micrometer.observation.annotation.Observed
import io.micrometer.observation.aop.ObservedAspect
import mu.KotlinLogging
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.*


@ControllerAdvice
class GlobalExceptionHandler {
    private val logger = KotlinLogging.logger {}


    @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR, reason = "Internal Server Error")
    @ExceptionHandler(RuntimeException::class)
    fun customerNotFound(exception: RuntimeException) {
        logger.error (exception){ "Exception" }
    }
}

// controller

@RestController
@RequestMapping("/message")
class MessageController(val meterRegistry: MeterRegistry) {

    private val logger = KotlinLogging.logger {}


    @Observed(name = "user.name",
        contextualName = "getting-user-name",
        lowCardinalityKeyValues = ["userType","userType2"])
    @GetMapping( "/ok")
    fun otpOk(): String {
        logger.info { "Otp ok" }
        val randomTime = (0..2000).random()
        try {
            Thread.sleep(randomTime.toLong())
        } catch (e: InterruptedException) {
        }
        return  "ok"
    }
    @GetMapping( "/ko")
    fun otpFail(
    ): String {
        logger.info { "Otp ko" }
        return  "ko"
    }


    @GetMapping( "/exception")
    fun exception(
    ): String {
        logger.info { "Otp Exception" }
        val randomTime = (0..5000).random()
        try {
            Thread.sleep(randomTime.toLong())
        } catch (e: InterruptedException) {
        }
        throw NullPointerException("crash")
        return  "exception"
    }
}


@SpringBootApplication
class KotlinWebApplication

fun main(args: Array<String>) {
    runApplication<KotlinWebApplication>(*args)
}

