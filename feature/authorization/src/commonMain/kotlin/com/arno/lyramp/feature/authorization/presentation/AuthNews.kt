package com.arno.lyramp.feature.authorization.presentation

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType

internal sealed interface AuthNews {
        data object NavigateToOnboarding : AuthNews
        data object NavigateToApplePlaylistInput : AuthNews
        data object NavigateToOptionalPlaylistInput : AuthNews
        data class LaunchAuth(val url: String, val service: MusicServiceType) : AuthNews
}