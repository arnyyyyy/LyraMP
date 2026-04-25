package com.arno.lyramp.feature.authorization.data

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.russhwolf.settings.Settings
import com.arno.lyramp.util.Log
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class YandexAuthRepository(settings: Settings) {
        private val storage = YandexAuthStorage(settings)
        val service: MusicServiceType = MusicServiceType.YANDEX

        fun getAccessToken(): String? = storage.accessToken
        fun provideValidAccessToken(): String? {
                val token = getAccessToken()
                if (token.isNullOrBlank()) return null
                if (storage.isTokenValid()) return token
                return null
        }

        fun handleAuthCallback(code: String) {
                try {
                        val token = code.substringBefore("_token_expiresIn_")
                        val expiresInStr = code.substringAfter("_token_expiresIn_")
                        val expiresIn = expiresInStr.toLongOrNull()

                        saveAccessToken(token, expiresIn)
                        AuthSelectionStorage.lastAuthorizedService = MusicServiceType.YANDEX.name

                } catch (e: Throwable) {
                        Log.logger.e(e) { "YandexAuthRepository: handleAuthCallback failed" }
                }
        }

        @OptIn(ExperimentalTime::class)
        fun saveAccessToken(token: String, expiresIn: Long?) {
                storage.accessToken = token
                storage.expiresIn = expiresIn?.let { it * 1000 + Clock.System.now().toEpochMilliseconds() }
                AuthSelectionStorage.lastAuthorizedService = MusicServiceType.YANDEX.name
        }
}
