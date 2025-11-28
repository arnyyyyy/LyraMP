package com.arno.lyramp.feature.authorization.presentation

sealed interface AuthNews {
        data object NavigateToHistory : AuthNews
}