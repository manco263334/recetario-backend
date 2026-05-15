package com.example.demo.application.use_cases.category

import com.example.demo.infrastructure.dto.CategoryDTO
import com.example.demo.core.utils.extension.isNeitherNullNorBlank
import com.example.demo.core.utils.mapper.toDTO
import com.example.demo.infrastructure.repository.CategoryRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

data class UpdateCategoryRequest (
    var id: String?,
    val name: String?,
    val icon: String?,
)

@Service
class UpdateCategory (
    private val repository : CategoryRepository
) {
    @Transactional
    fun execute (payload: UpdateCategoryRequest): CategoryDTO {
        val transaction = repository.findById(payload.id!!).getOrNull() ?: throw NoSuchElementException("Category with id '${payload.id}' not found")

        if (payload.name.isNeitherNullNorBlank()) {
            transaction.name = payload.name
        }

        if (payload.icon.isNeitherNullNorBlank()) {
            transaction.icon = payload.icon
        }

        repository.save(transaction)
        return transaction.toDTO()
    }
}