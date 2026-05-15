package com.example.demo.core.utils.mapper

import com.example.demo.domain.entities.RecipeEntity
import com.example.demo.domain.entities.UserEntity
import com.example.demo.domain.models.RecipeModel
import com.example.demo.domain.value_objects.Ingredient
import com.example.demo.infrastructure.dto.RecipeDTO

fun Ingredient.toMap(): Map<String, String> {
    return mapOf (
        "name" to this.name,
        "quantity" to this.quantity
    )
}

fun RecipeEntity.toDTO(): RecipeDTO {
    return RecipeDTO (
        id = this.id,
        name = this.name,
        persons = this.persons,
        ingredients = this.ingredients.toList(),
        steps = this.steps.toList(),
        totalTimeInMinutes = this.totalTimeInMinutes,
        cookingTimeInMinutes = this.cookingTimeInMinutes,
        preparationTimeInMinutes = this.preparationTimeInMinutes,
        stars = this.stars,
        icon = this.icon,

        creator = this.getCreator().toMap(),
        categories = this.getCategories().map {
            it.toMap()
        }
    )
}

fun RecipeEntity.toModel(): RecipeModel {
    return RecipeModel (
        id = this.id,
        name = this.name,
        persons = this.persons,
        ingredients = this.ingredients.map {
            Ingredient(it)
        },
        steps = this.steps,
        totalTimeInMinutes = this.totalTimeInMinutes,
        cookingTimeInMinutes = this.cookingTimeInMinutes,
        preparationTimeInMinutes = this.preparationTimeInMinutes,
        stars = this.stars,
        icon = this.icon
    )
}

fun RecipeEntity.toMap(): Map<String, Any?> {
    return mapOf (
        "id" to this.id,
        "name" to this.name,
        "persons" to this.persons,
        "ingredients" to this.ingredients,
        "steps" to this.steps,
        "totalTimeInMinutes" to this.totalTimeInMinutes,
        "cookingTimeInMinutes" to this.cookingTimeInMinutes,
        "preparationTimeInMinutes" to this.preparationTimeInMinutes,
        "stars" to this.stars,
        "icon" to this.icon
    )
}

fun RecipeModel.toEntity(creator: UserEntity): RecipeEntity {
    return RecipeEntity (
        id = this.id,
        name = this.name,
        persons = this.persons,
        ingredients = this.ingredients.map {
            it.toMap()
        },
        steps = this.steps,
        totalTimeInMinutes = this.totalTimeInMinutes,
        cookingTimeInMinutes = this.cookingTimeInMinutes,
        preparationTimeInMinutes = this.preparationTimeInMinutes,
        stars = this.stars,
        icon = this.icon,
        creator = creator
    )
}

fun RecipeModel.toDTO(): RecipeDTO {
    return RecipeDTO (
        id = this.id,
        name = this.name,
        persons = this.persons,
        ingredients = this.ingredients.map {
            it.toMap()
        },
        steps = this.steps.toList(),
        totalTimeInMinutes = this.totalTimeInMinutes,
        cookingTimeInMinutes = this.cookingTimeInMinutes,
        preparationTimeInMinutes = this.preparationTimeInMinutes,
        stars = this.stars,
        icon = this.icon
    )
}

//fun RecipeDTO.toEntity(userPassword: String): RecipeEntity {
//    assert(this.creator != null)
//
//    return RecipeEntity (
//        id = this.id,
//        creator = this.creator!!.toEntity(userPassword),
//        name = this.name,
//        persons = this.persons,
//        ingredients = this.ingredients,
//        steps = this.steps,
//        totalTimeInMinutes = this.totalTimeInMinutes,
//        cookingTimeInMinutes = this.cookingTimeInMinutes,
//        preparationTimeInMinutes = this.preparationTimeInMinutes,
//        stars = this.stars ?: 0,
//        icon = this.icon
//    )
//}

fun RecipeDTO.toEntity(creator: UserEntity): RecipeEntity {
    return RecipeEntity (
        id = this.id,
        creator = creator,
        name = this.name,
        persons = this.persons,
        ingredients = this.ingredients,
        steps = this.steps,
        totalTimeInMinutes = this.totalTimeInMinutes,
        cookingTimeInMinutes = this.cookingTimeInMinutes,
        preparationTimeInMinutes = this.preparationTimeInMinutes,
        stars = this.stars ?: 0,
        icon = this.icon
    )
}

fun RecipeDTO.toModel(): RecipeModel {
    return RecipeModel (
        id = this.id,
        name = this.name,
        persons = this.persons,
        ingredients = this.ingredients.map {
            Ingredient(it)
        },
        steps = this.steps,
        totalTimeInMinutes = this.totalTimeInMinutes,
        cookingTimeInMinutes = this.cookingTimeInMinutes,
        preparationTimeInMinutes = this.preparationTimeInMinutes,
        stars = this.stars ?: 0,
        icon = this.icon
    )
}