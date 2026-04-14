package com.arno.lyramp.feature.authorization.presentation

internal data class AuthState(
        val isLoading: Boolean = false,
        val error: String? = null
)