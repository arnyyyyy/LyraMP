package com.arno.lyramp.feature.authorization.presentation.spotify

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

object SpotifyAuthHolder {
        private val _authCodeFlow = MutableSharedFlow<String>(extraBufferCapacity = 1)
        val authCodeFlow: SharedFlow<String> = _authCodeFlow.asSharedFlow()

        fun emit(code: String) {
                _authCodeFlow.tryEmit(code)
        }

        @Suppress("unused")
        fun handleRedirect(url: String) {
                handleSpotifyRedirect(url)
        }
}

expect fun handleSpotifyRedirect(url: String)
