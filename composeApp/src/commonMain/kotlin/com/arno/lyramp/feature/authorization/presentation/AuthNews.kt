package com.arno.lyramp.feature.authorization.presentation

sealed interface AuthNews {
        data object NavigateToOnboarding : AuthNews
        data object NavigateToAppleEnterPlaylist : AuthNews
}