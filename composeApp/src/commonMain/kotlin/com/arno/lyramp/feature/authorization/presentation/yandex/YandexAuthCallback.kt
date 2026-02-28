package com.arno.lyramp.feature.authorization.presentation.yandex

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class YandexAuthResult(val token: String, val expiresIn: Long?)

object YandexAuthHolder {
        private val _authResultFlow = MutableSharedFlow<YandexAuthResult>(extraBufferCapacity = 1)
        val authResultFlow: SharedFlow<YandexAuthResult> = _authResultFlow.asSharedFlow()

        fun emit(token: String, expiresIn: Long?) {
                _authResultFlow.tryEmit(YandexAuthResult(token, expiresIn))
        }
}
