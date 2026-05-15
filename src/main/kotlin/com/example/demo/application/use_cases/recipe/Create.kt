package com.example.demo.application.use_cases.recipe

import com.example.demo.domain.entities.RecipeEntity
import com.example.demo.domain.entities.UserEntity
import com.example.demo.infrastructure.dto.RecipeDTO
import com.example.demo.core.utils.mapper.toDTO
import com.example.demo.infrastructure.repository.CategoryRepository
import com.example.demo.infrastructure.repository.RecipeRepository
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

data class CreateRecipeRequest (
    val name: String,
    val persons: Int,
    val ingredients: List<Map<String, String>>,
    val steps: List<String>,
    val totalTimeInMinutes: Int,
    val cookingTimeInMinutes: Int,
    val preparationTimeInMinutes: Int,
    val stars: Int?,
    val icon: String?,

    val categories: List<String>?,
)

@Service
class CreateRecipe (
    private val repository: RecipeRepository,
    private val categoryRepository: CategoryRepository
) {
    fun execute (payload: CreateRecipeRequest, user: UserEntity): RecipeDTO {
        val recipe = RecipeEntity (
            id = null,
            name = payload.name,
            persons = payload.persons,
            ingredients = payload.ingredients,
            steps = payload.steps,
            totalTimeInMinutes = payload.totalTimeInMinutes,
            cookingTimeInMinutes = payload.cookingTimeInMinutes,
            preparationTimeInMinutes = payload.preparationTimeInMinutes,
            stars = payload.stars ?: 0,
            icon = payload.icon,

            creator = user
        )

        if (payload.categories != null) {
            val categories = payload.categories.mapNotNull {
                categoryRepository.findById(it).getOrNull()
            }

            recipe.updateCategories(categories)
        }
        
        return repository.save(recipe).toDTO()
    }
}