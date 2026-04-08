package com.arno.lyramp.feature.authorization.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.authorization.domain.AuthService
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthorizationScreenModel(
        private val authService: AuthService,
        private val updateHandler: AuthUpdateHandler = AuthUpdateHandler()
) : ScreenModel {
        private val _state = MutableStateFlow(AuthState())
        val state: StateFlow<AuthState> = _state

        private val _news = Channel<AuthNews>(Channel.BUFFERED)
        val news = _news.receiveAsFlow()

        fun onEvent(event: AuthEvent) {
                when (event) {
                        is AuthEvent.OnLoginClick -> {
                                if (event.service == MusicServiceType.APPLE) {
                                        processUpdate(AuthUpdate.SuccessNavigate(MusicServiceType.APPLE))
                                        return
                                }

                                processUpdate(AuthUpdate.Loading)

                                screenModelScope.launch {
                                        runCatching {
                                                authService.initAuth(event.service)
                                        }.onSuccess { url ->
                                                _news.send(AuthNews.LaunchAuth(url, event.service))
                                                processUpdate(AuthUpdate.Finish)
                                        }.onFailure {
                                                processUpdate(
                                                        AuthUpdate.Error(
                                                                it.message ?: "Unknown error"
                                                        )
                                                )
                                        }
                                }
                        }

                        is AuthEvent.OnAuthCodeReceived -> {
                                processUpdate(AuthUpdate.Loading)

                                screenModelScope.launch {
                                        runCatching {
                                                authService.handleAuthCallback(
                                                        event.service,
                                                        event.code
                                                )
                                        }.onSuccess {
                                                processUpdate(AuthUpdate.SuccessNavigate(event.service))
                                                processUpdate(AuthUpdate.Finish)
                                        }.onFailure {
                                                processUpdate(
                                                        AuthUpdate.Error(
                                                                it.message ?: "Unknown error"
                                                        )
                                                )
                                        }
                                }
                        }
                }
        }

        private fun processUpdate(update: AuthUpdate) {
                val result = updateHandler.handle(_state.value, update)
                _state.value = result.state
                result.news?.let { news ->
                        screenModelScope.launch { _news.send(news) }
                }
        }
}
