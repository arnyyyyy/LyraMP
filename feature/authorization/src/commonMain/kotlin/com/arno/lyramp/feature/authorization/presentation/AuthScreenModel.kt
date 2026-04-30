package com.arno.lyramp.feature.authorization.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.YANDEX_AUTH_URL
import com.arno.lyramp.feature.authorization.domain.CompleteYandexLoginUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class AuthorizationScreenModel(
        private val completeYandexLogin: CompleteYandexLoginUseCase,
        private val reducer: AuthReducer = AuthReducer()
) : ScreenModel {
        private val _state = MutableStateFlow(AuthState())
        val state: StateFlow<AuthState> = _state.asStateFlow()

        private val _news = Channel<AuthNews>(Channel.BUFFERED)
        val news = _news.receiveAsFlow()

        fun onEvent(event: AuthEvent) {
                when (event) {
                        is AuthEvent.OnLoginClick -> {
                                when (event.service) {
                                        MusicServiceType.NONE -> {
                                                processUpdate(AuthUpdate.SuccessNavigate(MusicServiceType.NONE))
                                                return
                                        }

                                        MusicServiceType.YANDEX -> {
                                                processUpdate(AuthUpdate.Loading)

                                                screenModelScope.launch {
                                                        _news.send(AuthNews.LaunchAuth(YANDEX_AUTH_URL, event.service))
                                                        processUpdate(AuthUpdate.Finish)
                                                }
                                        }
                                }
                        }

                        is AuthEvent.OnYandexAuthCompleted -> {
                                processUpdate(AuthUpdate.Loading)
                                screenModelScope.launch {
                                        try {
                                                completeYandexLogin(event.token, event.expiresIn)
                                                Result.success(Unit)
                                        } catch (e: CancellationException) {
                                                throw e
                                        } catch (e: Throwable) {
                                                Result.failure(e)
                                        }.onSuccess {
                                                processUpdate(AuthUpdate.SuccessNavigate(MusicServiceType.YANDEX))
                                                processUpdate(AuthUpdate.Finish)
                                        }.onFailure {
                                                processUpdate(AuthUpdate.Error(it.message ?: "Unknown error"))
                                        }
                                }
                        }
                }
        }

        private fun processUpdate(update: AuthUpdate) {
                val result = reducer.reduce(_state.value, update)
                _state.value = result.state
                result.news?.let { _news.trySend(it) }
        }
}
