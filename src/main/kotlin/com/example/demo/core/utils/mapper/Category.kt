package com.example.demo.core.utils.mapper

import com.example.demo.domain.entities.CategoryEntity
import com.example.demo.infrastructure.dto.CategoryDTO

fun CategoryEntity.toMap(): Map<String, Any?> {
    return mapOf (
        "id" to id,
        "name" to name,
        "icon" to icon
    )
}

fun CategoryEntity.toDTO(): CategoryDTO {
    return CategoryDTO (
        id = this.id,
        name = this.name,
        icon = this.icon,

        recipes = this.getRecipes().map {
            it.toMap()
        }
    )
}

fun CategoryDTO.toEntity(): CategoryEntity {
    return CategoryEntity (
        id = id,
        name = this.name,
        icon = this.icon
    )
}