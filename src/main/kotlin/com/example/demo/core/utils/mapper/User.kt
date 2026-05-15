package com.example.demo.core.utils.mapper

import com.example.demo.domain.entities.UserEntity
import com.example.demo.domain.models.UserModel
import com.example.demo.infrastructure.dto.UserDTO
import com.example.demo.domain.value_objects.Email
import com.example.demo.domain.value_objects.Roles

fun roleGetter(role: String): Roles {
    val map = mapOf (
        "USER" to Roles.USER,
        "ADMIN" to Roles.ADMIN
    )

    return map.getOrDefault(role, Roles.USER)
}

fun UserEntity.toMap(): Map<String, Any?> {
    return mapOf (
        "id" to id,
        "name" to name,
        "email" to email,
        "username" to username,
        "password" to password,
        "phone" to phone,
        "username" to username,
        "icon" to icon
    )
}

fun UserEntity.toDTO(): UserDTO {
    return UserDTO (
        id = this.id,
        email = this.email,
        role = this.role,
        phone = this.phone,
        username = this.username,
        icon = this.icon,
        name = this.name,

        recipes = this.getRecipes().map {
            it.toMap()
        }
    )
}

fun UserEntity.toModel(): UserModel {
    return UserModel (
        id = this.id,
        name = this.name,
        email = Email(this.email),
        role = roleGetter(this.role),
        password = this.password,
        phone = this.phone,
        username = this.username,
        icon = this.icon,

        recipes = this.getRecipes().mapNotNull {
            it.id
        }
    )
}

fun UserDTO.toEntity(password: String): UserEntity {
    return UserEntity (
        id = this.id,
        password = password,
        role = this.role,
        phone = this.phone,
        username = this.username,
        icon = this.icon,
        name = this.name,
        email = this.email
    )
}

fun UserDTO.toModel(password: String): UserModel {
    return UserModel (
        id = this.id,
        name = this.name,
        email = Email(this.email),
        role = roleGetter(this.role),
        password = password,
        phone = this.phone,
        username = this.username,
        icon = this.icon
    )
}

fun UserModel.toEntity(): UserEntity {
    return UserEntity (
        id = this.id,
        name = this.name,
        email = this.email.toString(),
        role = this.role.toString(),
        password = this.password,
        phone = this.phone,
        username = this.username,
        icon = this.icon
    )
}

fun UserModel.toDTO(): UserDTO {
    return UserDTO (
        id = this.id,
        name = this.name,
        email = this.email.toString(),
        role = this.role.name,
        phone = this.phone,
        username = this.username,
        icon = this.icon
    )
}