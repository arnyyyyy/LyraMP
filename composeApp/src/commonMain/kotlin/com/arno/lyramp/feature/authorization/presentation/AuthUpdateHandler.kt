package com.arno.lyramp.feature.authorization.presentation

import com.arno.lyramp.feature.authorization.model.MusicServiceType

class AuthUpdateHandler {
        fun handle(state: AuthState, update: AuthUpdate): AuthCommand {
                return when (update) {
                        AuthUpdate.Loading -> AuthCommand(state = state.copy(isLoading = true, error = null))
                        AuthUpdate.Finish -> AuthCommand(state = state.copy(isLoading = false))

                        is AuthUpdate.Error -> AuthCommand(state = state.copy(isLoading = false, error = update.message))

                        is AuthUpdate.SuccessNavigate -> {
                                when (update.musicService) {
                                        MusicServiceType.SPOTIFY ->
                                                AuthCommand(
                                                        state = state.copy(isLoading = false),
                                                        news = AuthNews.NavigateToOnboarding
                                                )

                                        MusicServiceType.YANDEX ->
                                                AuthCommand(
                                                        state = state.copy(isLoading = false),
                                                        news = AuthNews.NavigateToOnboarding
                                                )

                                        MusicServiceType.APPLE ->
                                                AuthCommand(
                                                        state = state.copy(isLoading = false),
                                                        news = AuthNews.NavigateToAppleEnterPlaylist
                                                )
                                }
                        }
                }
        }
}
