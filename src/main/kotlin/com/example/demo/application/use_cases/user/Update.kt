package com.example.demo.application.use_cases.user

import com.example.demo.infrastructure.dto.UserDTO
import com.example.demo.core.utils.extension.isNeitherNullNorBlank
import com.example.demo.core.utils.mapper.toDTO
import com.example.demo.infrastructure.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.jvm.optionals.getOrNull

data class UpdateUserRequest(
    var id: String?,
    val name: String?,
    val email: String?,
    val icon: String?,
    val role: String?,
    val phone: String?,
    val username: String?
)

@Service
class UpdateUser (
    private val repository: UserRepository
) {
    @Transactional
    fun execute (payload: UpdateUserRequest): UserDTO {
        val transaction = repository.findById(payload.id!!).getOrNull() ?: throw NoSuchElementException("User with id '${payload.id}' not found")

        if (payload.email.isNeitherNullNorBlank()) {
            transaction.email = payload.email
        }

        if (payload.icon.isNeitherNullNorBlank()) {
            transaction.icon = payload.icon
        }

        if (payload.name.isNeitherNullNorBlank()) {
            transaction.name = payload.name
        }

        if (payload.role.isNeitherNullNorBlank()) {
            transaction.role = payload.role
        }

        if (payload.phone.isNeitherNullNorBlank()) {
            transaction.phone = payload.phone
        }

        if (payload.username.isNeitherNullNorBlank()) {
            transaction.username = payload.username
        }

        repository.save(transaction)
        return transaction.toDTO()
    }
}