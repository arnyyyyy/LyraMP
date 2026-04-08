package com.arno.lyramp.feature.authorization.repository

import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.russhwolf.settings.Settings
import com.arno.lyramp.util.Log
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class YandexAuthRepository(settings: Settings) : AuthApiRepository {

        private val storage = YandexAuthStorage(settings)

        override val service: MusicServiceType = MusicServiceType.YANDEX

        override fun getAccessToken(): String? = storage.accessToken

        override fun getRefreshToken(): String? = getAccessToken()

        override suspend fun refreshAccessToken(): String? = null

        override suspend fun provideValidAccessToken(): String? {
                val token = getAccessToken()
                if (token.isNullOrBlank()) return null
                if (storage.isTokenValid()) return token
                return null
        }

        // TODO ОГРОМНЫЙ
        override suspend fun initAuthFlow(): String {
                return YANDEX_AUTH_URL
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
                storage.accessToken = token
                storage.expiresIn = expiresIn?.let { it * 1000 + Clock.System.now().toEpochMilliseconds() }
                AuthSelectionStorage.lastAuthorizedService = MusicServiceType.YANDEX.name
        }

        private companion object {
                const val YANDEX_AUTH_URL = "https://passport.yandex.ru/auth?origin=music_app" +
                    "&retpath=https%3A%2F%2Foauth.yandex.ru%2Fauthorize%3Fresponse_type%3Dtoken" +
                    "%26client_id%3D23cabbbdc6cd418abb4b39c32c41195d%26redirect_uri" +
                    "%3Dhttps%253A%252F%252Fmusic.yandex.ru%252F%26force_confirm" +
                    "%3DFalse%26language%3Dru"
        }
}
