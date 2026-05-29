package com.example.demo.infrastructure.security.jwt

import com.example.demo.infrastructure.http.service.JWTService
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.HttpHeaders
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.core.userdetails.UserDetailsService
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JWTAuthenticationFilter (
    private final val jwtService: JWTService,
    private final val userDetailsService: UserDetailsService
): OncePerRequestFilter() {
    private final val AUTH_TOKEN = "AUTH_TOKEN"

    override fun doFilterInternal (
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val tokenFromRequest = getTokenFromRequest(request)
        val tokenFromCookie = getTokenFromCookie(request)

        val token = tokenFromCookie ?: tokenFromRequest ?: return filterChain.doFilter(request, response)

        val username = jwtService.getUsernameFromToken(token)

        if (username != null && SecurityContextHolder.getContext().authentication == null) {
            val userDetails = userDetailsService.loadUserByUsername(username)

            if (jwtService.isTokenValid(token, userDetails)) {
                val authToken = UsernamePasswordAuthenticationToken(userDetails, null, userDetails.authorities)

                authToken.details = WebAuthenticationDetailsSource().buildDetails(request)

                SecurityContextHolder.getContext().authentication = authToken
            }
        }

        filterChain.doFilter(request, response)
    }

    private fun getTokenFromRequest(request: HttpServletRequest): String? {
        val authHeader: String? = request.getHeader(HttpHeaders.AUTHORIZATION)
        val BEARER = "Bearer "

        return authHeader?.let {
            if (it.startsWith(BEARER)) {
                it.substring(BEARER.length)
            } else{
                null
            }
        }
    }

    private fun getTokenFromCookie(request: HttpServletRequest): String? {
        return request.cookies?.find { it.name == AUTH_TOKEN }?.value
    }
}