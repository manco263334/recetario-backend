package com.example.demo.infrastructure.security.handler

import jakarta.servlet.ServletException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.security.authorization.AuthorizationDeniedException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

@RestControllerAdvice
class GlobalHandlerException {
    @ExceptionHandler(IllegalArgumentException::class)
    fun handleBadRequest(ex: IllegalArgumentException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
            .body(mapOf (
                "error" to "Datos inválidos",
                "message" to (ex.message ?: "Error desconocido")
            ))
    }

    @ExceptionHandler(AuthorizationDeniedException::class)
    fun handleAuthorizationDenied(ex: AuthorizationDeniedException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(mapOf (
                "error" to "Acceso denegado",
                "message" to (ex.message ?: "No has iniciado sesión")
            ))
    }

    @ExceptionHandler(AccessDeniedException::class)
    fun handleAccessDenied(ex: AccessDeniedException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
            .body(mapOf (
                    "error" to "Acceso denegado",
                    "message" to "No tienes los permisos necesarios para realizar esta acción.",
                    "details" to (ex.message ?: "Sin detalles adicionales")
            ))
    }

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(ex: NoSuchElementException): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
            .body(mapOf (
                "error" to "No encontrado",
                "message" to (ex.message ?: "El recurso no existe")
            ))
    }

    @ExceptionHandler(ServletException::class)
    fun handleServletException(ex: ServletException): ResponseEntity<Map<String, String>> {
        val cause = ex.cause
        // Si la causa original es nuestra excepción de "no encontrado", la manejamos como tal
        if (cause is NoSuchElementException || ex.message?.contains("not found") == true) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(mapOf (
                    "error" to "No encontrado",
                    "message" to (cause?.message ?: ex.message ?: "")
                ))
        }
        return handleGeneralError(ex)
    }

    @ExceptionHandler(Exception::class)
    fun handleGeneralError(ex: Exception): ResponseEntity<Map<String, String>> {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
            .body(mapOf (
                "error" to "Error interno",
                "message" to (ex.message ?: "Algo explotó en el servidor")
            ))
    }
}