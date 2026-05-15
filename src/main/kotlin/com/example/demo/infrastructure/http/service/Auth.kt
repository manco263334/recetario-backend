package com.example.demo.infrastructure.http.service

import com.example.demo.application.use_cases.user.CreateUser
import com.example.demo.domain.models.UserModel
import com.example.demo.infrastructure.http.controller.AuthResponse
import com.example.demo.infrastructure.http.controller.MeResponse
import com.example.demo.core.utils.mapper.toEntity
import com.example.demo.core.utils.mapper.toModel
import com.example.demo.infrastructure.repository.UserRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import kotlin.Error

data class LoginRequest (
    val email: String,
    val password: String
)

@Service
class AuthService (
    private val repository: UserRepository,
    private val jwt: JWTService,
    private val userCreateUseCase: CreateUser,
    private val passwordEncoder: PasswordEncoder,
    private val authenticationManager: AuthenticationManager
) {
    @CacheEvict (
        cacheNames = ["users"],
        allEntries = true
    )
    @Cacheable (
        cacheNames = ["users"],
        key = "#transaction.id"
    )
    fun register (data: UserModel): AuthResponse {
        val user = repository.findByEmail(data.email.toString())

        if (user != null) {
            throw Error("User already exists")
        }

        data.password = passwordEncoder.encode(data.password)!!
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(data.email, data.password))

        val transaction = userCreateUseCase.execute(data.toEntity())
        val token = jwt.getToken(mapOf("id" to transaction.id!!), transaction.toModel(data.password))

        return AuthResponse (
            token = token,
            name = transaction.name,
            username = transaction.username
        )
    }

    fun login (data: LoginRequest): AuthResponse {
        authenticationManager.authenticate(UsernamePasswordAuthenticationToken(data.email, data.password))
        val user = repository.findByEmail(data.email)?.toModel() ?: throw NoSuchElementException("User with email '${data.email}' not found")
        val token: String = jwt.getToken(mapOf("id" to user.id!!), user)
        return AuthResponse (
            token = token,
            name = user.name,
            username = user.username
        )
    }

    fun me(): MeResponse {
        val user = SecurityContextHolder.getContext().authentication!!.principal as UserModel

        return MeResponse (
            id = user.id!!,
            email = user.email.toString(),
            name = user.name,
            username = user.username
        )
    }
}