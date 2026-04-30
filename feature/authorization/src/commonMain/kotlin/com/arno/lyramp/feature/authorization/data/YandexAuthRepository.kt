package com.arno.lyramp.feature.authorization.data

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.russhwolf.settings.Settings
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class YandexAuthRepository(settings: Settings) {
        private val storage = YandexAuthStorage(settings)

        private fun getAccessToken(): String? = storage.accessToken

        fun provideValidAccessToken(): String? {
                val token = getAccessToken()
                if (token.isNullOrBlank()) return null
                if (storage.isTokenValid()) return token
                return null
        }

        @OptIn(ExperimentalTime::class)
        fun saveAccessToken(token: String, expiresIn: Long?) {
                storage.accessToken = token
                storage.expiresIn = expiresIn?.let { it * 1000 + Clock.System.now().toEpochMilliseconds() }
                AuthSelectionStorage.lastAuthorizedService = MusicServiceType.YANDEX.name
        }
}
