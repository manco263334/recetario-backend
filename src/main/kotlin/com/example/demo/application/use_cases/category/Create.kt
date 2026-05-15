package com.example.demo.application.use_cases.category

import com.example.demo.domain.entities.CategoryEntity
import com.example.demo.infrastructure.dto.CategoryDTO
import com.example.demo.core.utils.mapper.toDTO
import com.example.demo.infrastructure.repository.CategoryRepository
import org.springframework.stereotype.Service

@Service
class CreateCategory (
    private val repository: CategoryRepository
) {
    fun execute (payload: CategoryEntity): CategoryDTO {
        return repository.save(payload).toDTO()
    }
}