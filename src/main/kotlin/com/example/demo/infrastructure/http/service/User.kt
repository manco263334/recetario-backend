package com.example.demo.infrastructure.http.service

import com.example.demo.application.use_cases.user.UpdateUser
import com.example.demo.application.use_cases.user.UpdateUserRequest
import com.example.demo.infrastructure.dto.UserDTO
import com.example.demo.core.utils.extension.isNullOrFalse
import com.example.demo.core.utils.mapper.toDTO
import com.example.demo.infrastructure.repository.UserRepository
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.Cacheable
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.stereotype.Service
import kotlin.jvm.optionals.getOrNull

@Service
class UserService (
    private val repository: UserRepository,
    private val userUpdateUseCase: UpdateUser
) {
    @Cacheable (
        cacheNames = ["users"],
        key = "#id + '-' + #withRecipes"
    )
    fun findById(id: String, withRecipes: Boolean?): UserDTO? {
        val user = repository.findById(id).getOrNull()?.toDTO()

        if (withRecipes.isNullOrFalse() && user != null) {
            user.recipes = null
        }

        return user
    }

    @Cacheable (
        cacheNames = ["users"],
        key = "#pagebale.pageNumber + '-' + #withRecipes"
    )
    fun findAll(pageable: Pageable, withRecipes: Boolean?): Page<UserDTO> {
        val users = repository.findAll(pageable).map { it.toDTO() }

        if (withRecipes.isNullOrFalse()) {
            return users.map { it.recipes = null; it }
        }

        return users
    }

    @CacheEvict (
        cacheNames = ["users"],
        allEntries = true,
        key = "#id"
    )
    @Cacheable (
        cacheNames = ["users"],
        key = "#id"
    )
    fun update(id: String, data: UpdateUserRequest): UserDTO {
        data.id = id
        return userUpdateUseCase.execute(data)
    }

    @CacheEvict (
        cacheNames = ["users"],
        allEntries = true,
        key = "#id"
    )
    fun delete(id: String) {
        repository.deleteById(id)
    }
}