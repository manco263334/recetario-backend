package com.example.demo.infrastructure.http.service

import com.example.demo.application.use_cases.recipe.CreateRecipe
import com.example.demo.application.use_cases.recipe.CreateRecipeRequest
import com.example.demo.application.use_cases.recipe.UpdateRecipe
import com.example.demo.application.use_cases.recipe.UpdateRecipeRequest
import com.example.demo.domain.entities.UserEntity
import com.example.demo.infrastructure.dto.RecipeDTO
import com.example.demo.core.utils.extension.isNullOrFalse
import com.example.demo.core.utils.mapper.toDTO
import com.example.demo.infrastructure.repository.RecipeRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class RecipeService (
    private val repository: RecipeRepository,
    private val createRecipeUseCase: CreateRecipe,
    private val updateRecipeUseCase: UpdateRecipe
) {
    @CacheEvict (
        cacheNames = ["recipes"],
        allEntries = true
    )
    fun save (
        data: CreateRecipeRequest,
        user: UserEntity
    ): RecipeDTO {
        return createRecipeUseCase.execute(data, user)
    }

    @Cacheable (
        cacheNames = ["recipes"],
        key = "#pageable.pageNumber + '-' + #withCategories + '-' + #withCreator",
    )
    fun findAll (
        pageable: Pageable,
        withCategories: Boolean?,
        withCreator: Boolean?
    ): Page<RecipeDTO> {
        var recipes = repository.findAll(pageable).map { it.toDTO() }

        if (withCategories.isNullOrFalse()) {
            recipes = recipes.map { it.categories = null; it }
        }

        if (withCreator.isNullOrFalse()) {
            recipes = recipes.map { it.creator = null; it }
        }

        return recipes
    }

    @Cacheable (
        cacheNames = ["recipes"],
        key = "#id + '-' + #withCategories + '-' + #withCreator",
    )
    fun findById (
        id: String,
        withCategories: Boolean?,
        withCreator: Boolean?
    ): RecipeDTO? {
        val recipe = repository.findById(id).getOrNull()?.toDTO()

        if (withCategories.isNullOrFalse() && recipe != null) {
            recipe.categories = null
        }

        if (withCreator.isNullOrFalse() && recipe != null) {
            recipe.creator = null
        }

        return recipe
    }

    @CacheEvict (
        cacheNames = ["recipes"],
        allEntries = true,
        key = "#id",
    )
    @Cacheable (
        cacheNames = ["recipes"],
        key = "#id",
    )
    fun update (
        id: String,
        data: UpdateRecipeRequest
    ): RecipeDTO {
        data.id = id
        return updateRecipeUseCase.execute(data)
    }

    @CacheEvict (
        cacheNames = ["recipes"],
        allEntries = true,
        key = "#id",
    )
    fun delete (
        id: String
    ) {
        return repository.deleteById(id)
    }
}