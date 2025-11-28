package com.arno.lyramp.feature.authorization.domain.usecase

import com.arno.lyramp.feature.authorization.domain.AuthService
import com.arno.lyramp.feature.authorization.model.MusicServiceType

class HandleAuthCallbackUseCase(
        private val authService: AuthService
) {
        suspend operator fun invoke(service: MusicServiceType, code: String) {
                authService.handleAuthCallback(service, code)
        }
}
