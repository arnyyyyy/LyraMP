package com.arno.lyramp.feature.authorization.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.YANDEX_AUTH_URL
import com.arno.lyramp.feature.authorization.domain.CompleteYandexLoginUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.yandex.YandexAuthBus
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.launch

internal class AuthorizationScreenModel(
        private val completeYandexLogin: CompleteYandexLoginUseCase,
        private val yandexAuthBus: YandexAuthBus,
        private val reducer: AuthReducer = AuthReducer()
) : ScreenModel {
        private val _state = MutableStateFlow(AuthState())
        val state: StateFlow<AuthState> = _state.asStateFlow()

        private val _news = Channel<AuthNews>(Channel.BUFFERED)
        val news = _news.receiveAsFlow()

        init {
                screenModelScope.launch {
                        yandexAuthBus.flow.collectLatest { result ->
                                onEvent(
                                        AuthEvent.OnYandexAuthCompleted(
                                                token = result.token,
                                                expiresIn = result.expiresIn
                                        )
                                )
                                yandexAuthBus.consume()
                        }
                }
        }

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
                                        runCatching {
                                                completeYandexLogin(event.token, event.expiresIn)
                                        }.onSuccess {
                                                processUpdate(AuthUpdate.SuccessNavigate(MusicServiceType.YANDEX))
                                                processUpdate(AuthUpdate.Finish)
                                        }.onFailure {
                                                if (it is CancellationException) throw it
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
