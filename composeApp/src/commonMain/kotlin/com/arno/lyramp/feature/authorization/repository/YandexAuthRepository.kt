package com.arno.lyramp.feature.authorization.repository

import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.yandex.launchYandexAuth
import com.arno.lyramp.util.Log
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class YandexAuthRepository : AuthApiRepository {

        override val service: MusicServiceType = MusicServiceType.YANDEX

        override fun getAccessToken(): String? = YandexAuthStorage.accessToken

        override fun getRefreshToken(): String? = getAccessToken()

        override suspend fun refreshAccessToken(): String? = null

        override suspend fun provideValidAccessToken(): String? {
                val token = getAccessToken()

                if (token.isNullOrBlank()) {
                        return null
                }

                if (YandexAuthStorage.isTokenValid()) {
                        return token
                }
                return null
        }

        override suspend fun initAuthFlow() {
                launchYandexAuth()
        }

        override suspend fun handleAuthCallback(code: String) {
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
                YandexAuthStorage.accessToken = token
                YandexAuthStorage.expiresIn = expiresIn?.let { it * 1000 + Clock.System.now().toEpochMilliseconds() }

                try {
                        AuthSelectionStorage.lastAuthorizedService = MusicServiceType.YANDEX.name
                } catch (e: Throwable) {
                        Log.logger.e(e) { "YandexAuthRepository: token saving failed" }
                }
        }
}
