package com.kotlin.kotlinspring.web

import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry
import io.github.resilience4j.kotlin.circuitbreaker.decorateSuspendFunction
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.time.Duration
import kotlinx.coroutines.runBlocking

@RestController
@RequestMapping("/hello")
class HelloController {

    val circuitBreakerConfig =
        CircuitBreakerConfig.custom()
            .waitDurationInOpenState(Duration.ofMillis(5000))
            .maxWaitDurationInHalfOpenState(Duration.ofMillis(5000))
            .permittedNumberOfCallsInHalfOpenState(5)
            .minimumNumberOfCalls(3)
            .build()

    val circuitBreaker = CircuitBreakerRegistry.of(circuitBreakerConfig).circuitBreaker("TECHTALKS")

    @GetMapping("/{id}")
    private fun testTechTalks(@PathVariable id: Long): HttpStatus {
        return runBlocking {
            test(id)
        }
    }

    private suspend fun test(id: Long): HttpStatus {
        return circuitBreaker.decorateSuspendFunction {
            validate(id)
        }.let { suspendFunction ->
            try {
                suspendFunction()
            } catch (exception: Exception) {
                println("Erro ao chamar o serviço X: $exception")
                HttpStatus.INTERNAL_SERVER_ERROR
            }
        }
    }

    private fun validate(id: Long): HttpStatus {
        if (id != 1L) {
            throw RuntimeException()
        }
        println("O serviço X está funcionando normalmente!")
        println("CircuitBreaker '${circuitBreaker.name}' is " + circuitBreaker.state)
        return HttpStatus.OK
    }

}