package com.example.demo.application.use_cases.recipe

import com.example.demo.infrastructure.dto.RecipeDTO
import com.example.demo.core.utils.extension.isNeitherNullNorBlank
import com.example.demo.core.utils.mapper.toDTO
import com.example.demo.infrastructure.repository.CategoryRepository
import com.example.demo.infrastructure.repository.RecipeRepository
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

data class UpdateRecipeRequest (
    var id: String?,
    val name: String?,
    val persons: Int?,
    val ingredients: List<Map<String, String>>?,
    val steps: List<String>?,
    val totalTimeInMinutes: Int?,
    val cookingTimeInMinutes: Int?,
    val preparationTimeInMinutes: Int?,
    val stars: Int?,
    val icon: String?,

    val categories: List<String>?,
)

@Service
class UpdateRecipe (
    private val repository: RecipeRepository,
    private val categoryRepository: CategoryRepository
) {
    @Transactional
    fun execute (payload: UpdateRecipeRequest): RecipeDTO {
        val transaction = repository.findById(payload.id!!).getOrNull() ?: throw NoSuchElementException("Recipe with id '${payload.id}' not found")

        if (payload.name.isNeitherNullNorBlank()) {
            transaction.name = payload.name
        }

        if (payload.persons != null) {
            transaction.persons = payload.persons
        }

        if (payload.ingredients != null) {
            transaction.ingredients = payload.ingredients
        }

        if (payload.steps != null) {
            transaction.steps = payload.steps
        }

        if (payload.totalTimeInMinutes != null) {
            transaction.totalTimeInMinutes = payload.totalTimeInMinutes
        }

        if (payload.cookingTimeInMinutes != null) {
            transaction.cookingTimeInMinutes = payload.cookingTimeInMinutes
        }

        if (payload.preparationTimeInMinutes != null) {
            transaction.preparationTimeInMinutes = payload.preparationTimeInMinutes
        }

        if (payload.stars != null) {
            // TODO: Agregar lógica para obtener el promedio de estrellas en lugar de poner directamente la nueva
            transaction.stars = payload.stars
        }

        if (payload.icon.isNeitherNullNorBlank()) {
            transaction.icon = payload.icon
        }

        if (payload.categories != null) {
            val categories = payload.categories.mapNotNull {
                categoryRepository.findById(it).getOrNull()
            }

            transaction.updateCategories(categories)
        }

        repository.save(transaction)
        return transaction.toDTO()
    }
}