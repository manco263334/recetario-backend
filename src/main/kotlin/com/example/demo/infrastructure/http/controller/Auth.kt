package com.example.demo.infrastructure.http.controller

import com.example.demo.domain.entities.UserEntity
import com.example.demo.infrastructure.http.service.AuthService
import com.example.demo.infrastructure.http.service.LoginRequest
import com.example.demo.core.utils.mapper.toModel
import jakarta.servlet.http.Cookie
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

data class AuthResponse (
    val token: String,
    val name: String,
    val username: String?
)

data class MeResponse (
    val id: String,
    val email: String,
    val name: String,
    val username: String?
)

@RestController
@RequestMapping("/api/auth")
class AuthController (
    private val service: AuthService
) {
    private final val AUTH_TOKEN = "AUTH_TOKEN"

    @PostMapping("/login")
    fun login (
        @RequestBody data: LoginRequest,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        val loginResponse = service.login(data)
        val cookie = createCookie(loginResponse.token)

        response.addCookie(cookie)

        return ResponseEntity.ok(loginResponse)
    }

    @PostMapping("/register")
    fun register (
        @RequestBody data: UserEntity,
        response: HttpServletResponse
    ): ResponseEntity<AuthResponse> {
        val registerResponse = service.register(data.toModel())
        val cookie = createCookie(registerResponse.token)

        response.addCookie(cookie)

        return ResponseEntity.ok(registerResponse)
    }

    private fun createCookie(token: String): Cookie {
        return Cookie(AUTH_TOKEN, token).apply {
            isHttpOnly = true
            secure = true
            maxAge = 60 * 60 * 24 * 7
            path = "/"
            setAttribute("SameSite", "Strict")
        }
    }

    @PostMapping("/logout")
    fun logout() {
        Cookie(AUTH_TOKEN, null).apply {
            isHttpOnly = true
            maxAge = 0
            path = "/"
        }
    }

    @GetMapping("/me")
    @PreAuthorize("authentication?.principal != 'anonymousUser'")
    fun me(): ResponseEntity<MeResponse> {
        val response = service.me()

        return ResponseEntity.ok(response)
    }
}