package com.example.demo.infrastructure.http.service

import com.example.demo.application.use_cases.category.CreateCategory
import com.example.demo.application.use_cases.category.UpdateCategory
import com.example.demo.application.use_cases.category.UpdateCategoryRequest
import com.example.demo.domain.entities.CategoryEntity
import com.example.demo.infrastructure.dto.CategoryDTO
import com.example.demo.core.utils.extension.isNullOrFalse
import com.example.demo.core.utils.mapper.toDTO
import com.example.demo.infrastructure.repository.CategoryRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class CategoryService (
    private val repository: CategoryRepository,
    private val createCategoryUseCase: CreateCategory,
    private val updateCategoryUseCase: UpdateCategory,
) {
    @CacheEvict (
        cacheNames = ["categories"],
        allEntries = true
    )
    fun save (category: CategoryEntity): CategoryDTO {
        return createCategoryUseCase.execute(category)
    }

    @Cacheable (
        cacheNames = ["categories"],
        key = "#pageable.pageNumber + '-' + #withRecipes",
    )
    fun findAll (pageable: Pageable, withRecipes: Boolean?): Page<CategoryDTO> {
        var categories = repository.findAll(pageable).map { it.toDTO() }

        if (withRecipes.isNullOrFalse()) {
            categories = categories.map { it.recipes = null; it }
        }

        return categories
    }

    @Cacheable (
        cacheNames = ["categories"],
        key = "#id + '-' + #withRecipes"
    )
    fun findById (id: String, withRecipes: Boolean?): CategoryDTO? {
        val category = repository.findById(id).getOrNull()?.toDTO()

        if (withRecipes.isNullOrFalse() && category != null) {
            category.recipes = null
        }

        return category
    }

    @CacheEvict (
        cacheNames = ["categories"],
        allEntries = true,
        key = "#id"
    )
    @Cacheable (
        cacheNames = ["categories"],
        key = "#id"
    )
    fun update (id: String, data: UpdateCategoryRequest): CategoryDTO {
        data.id = id
        return updateCategoryUseCase.execute(data)
    }

    @CacheEvict (
        cacheNames = ["categories"],
        allEntries = true,
        key = "#id"
    )
    fun delete (id: String) {
        repository.deleteById(id)
    }
}