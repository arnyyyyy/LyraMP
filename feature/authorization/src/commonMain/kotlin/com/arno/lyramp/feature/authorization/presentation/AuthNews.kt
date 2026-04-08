package com.arno.lyramp.feature.authorization.presentation

import com.arno.lyramp.feature.authorization.model.MusicServiceType

sealed interface AuthNews {
        data object NavigateToOnboarding : AuthNews
        data object NavigateToAppleEnterPlaylist : AuthNews
        data class LaunchAuth(val url: String, val service: MusicServiceType) : AuthNews
}