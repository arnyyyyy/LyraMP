package com.arno.lyramp.feature.authorization.presentation

sealed interface AuthUpdate {
        object Loading : AuthUpdate
        object Finish : AuthUpdate
        data class Error(val message: String) : AuthUpdate
        object SuccessNavigate : AuthUpdate
        object SuccessNavigateToYandex : AuthUpdate
}

data class AuthCommand(
        val state: AuthState,
        val news: AuthNews? = null
)
