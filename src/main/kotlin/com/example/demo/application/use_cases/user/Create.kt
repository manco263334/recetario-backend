package com.example.demo.application.use_cases.user

import com.example.demo.domain.entities.UserEntity
import com.example.demo.domain.value_objects.Email
import com.example.demo.infrastructure.dto.UserDTO
import com.example.demo.core.utils.mapper.toDTO
import com.example.demo.infrastructure.repository.UserRepository
import org.springframework.stereotype.Service

@Service
class CreateUser (
    private val repository: UserRepository
) {
    fun execute (payload: UserEntity): UserDTO {
        val name = payload.name
        val email = Email(payload.email)
        val role = payload.role
        val password = payload.password
        val phone = payload.phone
        val username = payload.username
        val icon = payload.icon
        
        val transaction = UserEntity (
            name = name,
            email = email.email,
            role = role,
            password = password,
            phone = phone,
            username = username,
            icon = icon
        )

        return repository.save(transaction).toDTO()
    }
}