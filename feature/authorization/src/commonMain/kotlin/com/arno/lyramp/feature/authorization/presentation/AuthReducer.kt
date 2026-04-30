package com.arno.lyramp.feature.authorization.presentation

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType

internal class AuthReducer {
        fun reduce(state: AuthState, update: AuthUpdate): AuthUpdateResult {
                return when (update) {
                        AuthUpdate.Loading -> AuthUpdateResult(state = state.copy(isLoading = true, error = null))
                        AuthUpdate.Finish -> AuthUpdateResult(state = state.copy(isLoading = false))

                        is AuthUpdate.Error -> AuthUpdateResult(state = state.copy(isLoading = false, error = update.message))

                        is AuthUpdate.SuccessNavigate -> {
                                when (update.musicService) {
                                        MusicServiceType.YANDEX ->
                                                AuthUpdateResult(
                                                        state = state.copy(isLoading = false),
                                                        news = AuthNews.NavigateToOnboarding
                                                )

                                        MusicServiceType.NONE ->
                                                AuthUpdateResult(
                                                        state = state.copy(isLoading = false),
                                                        news = AuthNews.NavigateToOptionalPlaylistInput
                                                )
                                }
                        }
                }
        }
}
