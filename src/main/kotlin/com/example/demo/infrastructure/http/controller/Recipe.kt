package com.example.demo.infrastructure.http.controller

import com.example.demo.application.use_cases.recipe.CreateRecipeRequest
import com.example.demo.application.use_cases.recipe.UpdateRecipeRequest
import com.example.demo.domain.models.UserModel
import com.example.demo.infrastructure.dto.RecipeDTO
import com.example.demo.infrastructure.http.service.RecipeService
import com.example.demo.core.utils.mapper.toEntity
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.http.ResponseEntity
import org.springframework.security.access.prepost.PreAuthorize
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/recipes")
class RecipeController (
    private val service: RecipeService
) {
    @PostMapping("/", "")
    @PreAuthorize("authentication?.principal != 'anonymousUser'")
    fun create (
        @RequestBody data: CreateRecipeRequest
    ): RecipeDTO {
        val user = SecurityContextHolder.getContext().authentication!!.principal as UserModel

        val recipe = service.save(data, user.toEntity())
        recipe.creator = null
        return recipe
    }

    @GetMapping("/", "", "/all")
    @PreAuthorize("#withCreator == true ? hasRole('ADMIN') : true")
    fun getAll (
        @RequestParam(value = "page", defaultValue = "0") page: Int,
        @RequestParam(value = "size", defaultValue = "10") size: Int,
        @RequestParam(value = "withCategories", required = false) withCategories: Boolean?,
        @RequestParam(value = "withCreator", required = false) withCreator: Boolean?
    ): ResponseEntity<Page<RecipeDTO>> {
        val pageable = PageRequest.of(page, size)
        val response = service.findAll (
            pageable,
            withCategories = withCategories,
            withCreator = withCreator
        )

        return ResponseEntity.ok(response)
    }

    @GetMapping("/{id}")
    @PreAuthorize("#withCreator == true ? hasRole('ADMIN') : true")
    fun getById (
        @PathVariable id: String,
        @RequestParam(value = "withCategories", required = false) withCategories: Boolean?,
        @RequestParam(value = "withCreator", required = false) withCreator: Boolean?
    ): ResponseEntity<RecipeDTO> {
        val response = service.findById (
            id = id,
            withCategories = withCategories,
            withCreator = withCreator
        ) ?: throw NoSuchElementException("Recipe with id '$id' not found")

        return ResponseEntity.ok(response)
    }

    @PutMapping("/{id}")
    @PreAuthorize("authentication.principal.recipes.?[#this == #id].size() > 0")
    fun update (
        @PathVariable id: String,
        @RequestBody data: UpdateRecipeRequest
    ): ResponseEntity<RecipeDTO> {
        val response = service.update(id, data)

        return ResponseEntity.ok(response)
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("authentication.principal.recipes.?[#this == #id].size() > 0")
    fun delete (
        @PathVariable id: String
    ): ResponseEntity<Unit> {
        val response = service.delete(id)

        return ResponseEntity.ok(response)
    }
}