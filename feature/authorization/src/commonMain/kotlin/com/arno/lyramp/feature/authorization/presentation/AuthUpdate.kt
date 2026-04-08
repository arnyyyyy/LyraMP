package com.arno.lyramp.feature.authorization.presentation

import com.arno.lyramp.feature.authorization.model.MusicServiceType

sealed interface AuthUpdate {
        object Loading : AuthUpdate
        object Finish : AuthUpdate
        data class Error(val message: String) : AuthUpdate
        data class SuccessNavigate(val musicService: MusicServiceType) : AuthUpdate
}

data class AuthCommand(
        val state: AuthState,
        val news: AuthNews? = null
)
