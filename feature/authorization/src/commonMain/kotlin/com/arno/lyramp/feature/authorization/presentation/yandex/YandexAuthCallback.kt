package com.arno.lyramp.feature.authorization.presentation.yandex

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.channels.BufferOverflow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

data class YandexAuthResult(val token: String, val expiresIn: Long?)

class YandexAuthBus internal constructor() {
        private val _flow = MutableSharedFlow<YandexAuthResult>(
                replay = 1,
                extraBufferCapacity = 1,
                onBufferOverflow = BufferOverflow.DROP_OLDEST,
        )
        val flow: SharedFlow<YandexAuthResult> = _flow.asSharedFlow()

        fun emit(token: String, expiresIn: Long?) = _flow.tryEmit(YandexAuthResult(token, expiresIn))

        @OptIn(ExperimentalCoroutinesApi::class)
        fun consume() = _flow.resetReplayCache()
}

internal object YandexAuthBusProvider {
        private var instance: YandexAuthBus? = null

        fun set(bus: YandexAuthBus) {
                instance = bus
        }

        fun get(): YandexAuthBus = instance ?: error("YandexAuthBus is not initialized. Make sure authModule is loaded.")
}
