package com.arno.lyramp.feature.authorization.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.authorization.data.YandexAuthRepository
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class AuthorizationScreenModel(
        private val yandexRepo: YandexAuthRepository,
        private val updateHandler: AuthUpdateHandler = AuthUpdateHandler()
) : ScreenModel {
        private val _state = MutableStateFlow(AuthState())
        val state: StateFlow<AuthState> = _state.asStateFlow()

        private val _news = Channel<AuthNews>(Channel.BUFFERED)
        val news = _news.receiveAsFlow()

        fun onEvent(event: AuthEvent) {
                when (event) {
                        is AuthEvent.OnLoginClick -> {
                                when (event.service) {
                                        MusicServiceType.APPLE -> {
                                                processUpdate(AuthUpdate.SuccessNavigate(MusicServiceType.APPLE))
                                                return
                                        }

                                        MusicServiceType.NONE -> {
                                                processUpdate(AuthUpdate.SuccessNavigate(MusicServiceType.NONE))
                                                return
                                        }

                                        MusicServiceType.YANDEX -> {
                                                processUpdate(AuthUpdate.Loading)

                                                screenModelScope.launch {
                                                        try {
                                                                Result.success(yandexRepo.initAuthFlow())
                                                        } catch (e: CancellationException) {
                                                                throw e
                                                        } catch (e: Throwable) {
                                                                Result.failure(e)
                                                        }.onSuccess { url ->
                                                                _news.send(AuthNews.LaunchAuth(url, event.service))
                                                                processUpdate(AuthUpdate.Finish)
                                                        }.onFailure {
                                                                processUpdate(AuthUpdate.Error(it.message ?: "Unknown error"))
                                                        }
                                                }
                                        }
                                }
                        }

                        is AuthEvent.OnAuthCodeReceived -> {
                                processUpdate(AuthUpdate.Loading)
                                screenModelScope.launch {
                                        try {
                                                yandexRepo.handleAuthCallback(event.code)
                                                Result.success(Unit)
                                        } catch (e: CancellationException) {
                                                throw e
                                        } catch (e: Throwable) {
                                                Result.failure(e)
                                        }.onSuccess {
                                                processUpdate(AuthUpdate.SuccessNavigate(event.service))
                                                processUpdate(AuthUpdate.Finish)
                                        }.onFailure {
                                                processUpdate(AuthUpdate.Error(it.message ?: "Unknown error"))
                                        }
                                }
                        }
                }
        }

        private fun processUpdate(update: AuthUpdate) {
                val result = updateHandler.handle(_state.value, update)
                _state.value = result.state
                result.news?.let { _news.trySend(it) }
        }
}
