package com.example.demo.infrastructure.security.limiter

import io.github.bucket4j.Bandwidth
import io.github.bucket4j.Bucket
import io.github.bucket4j.Refill
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.time.Duration
import java.util.concurrent.ConcurrentHashMap

@Component
class RateLimitingFilter : OncePerRequestFilter() {
    private val buckets = ConcurrentHashMap<String, Bucket>()

    private fun createNewBucket(): Bucket {
        return Bucket.builder()
            .addLimit(Bandwidth.classic(5, Refill.intervally(5, Duration.ofSeconds(30))))
            .build()
    }

    override fun doFilterInternal (
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val key = request.remoteAddr
        val url = request.requestURL.toString()
        val skippedEndpoints = arrayOf("logout")

        if (skippedEndpoints.any { url.contains(it) }) {
            return filterChain.doFilter(request, response)
        }

        val userBucket = buckets.computeIfAbsent(key) { createNewBucket() }

        if (userBucket.tryConsume(1)) {
            filterChain.doFilter(request, response)
        } else {
            response.status = HttpStatus.TOO_MANY_REQUESTS.value()
            response.contentType = "application/json"
            response.writer.write("""{
                "error": "Too Many Requests",
                "message": "Cálmate pibe, vas muy rápido. Intenta de nuevo en un minuto."
            }""")
        }
    }
}