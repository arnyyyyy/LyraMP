package com.arno.lyramp.feature.authorization.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.authorization.domain.usecase.HandleAuthCallbackUseCase
import com.arno.lyramp.feature.authorization.domain.usecase.InitAuthUseCase
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

class AuthorizationScreenModel(
        private val initAuth: InitAuthUseCase,
        private val handleAuthCallback: HandleAuthCallbackUseCase,
        private val updateHandler: AuthUpdateHandler = AuthUpdateHandler()
) : ScreenModel {

        private val _state = MutableStateFlow(AuthState())
        val state: StateFlow<AuthState> = _state

        private val _news = Channel<AuthNews>(Channel.BUFFERED)
        val news = _news.receiveAsFlow()

        fun onEvent(event: AuthEvent) {
                when (event) {
                        is AuthEvent.OnLoginClick -> {
                                if (event.service == MusicServiceType.YANDEX) {
                                        processUpdate(AuthUpdate.SuccessNavigateToYandex)
                                        return
                                }

                                processUpdate(AuthUpdate.Loading)

                                screenModelScope.launch {
                                        runCatching {
                                                initAuth(event.service)
                                        }.onSuccess {
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
                                                handleAuthCallback(
                                                        event.service,
                                                        event.code
                                                )
                                        }.onSuccess {
                                                processUpdate(AuthUpdate.SuccessNavigate)
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
