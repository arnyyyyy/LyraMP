package com.arno.lyramp.feature.authorization.presentation

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType

internal sealed interface AuthEvent {
        data class OnLoginClick(val service: MusicServiceType) : AuthEvent
        data class OnAuthCodeReceived(val service: MusicServiceType, val code: String) : AuthEvent
}
