package com.arno.lyramp.feature.authorization.repository

import com.russhwolf.settings.Settings
import com.arno.lyramp.util.Log
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal object YandexAuthStorage {
        private val settings = Settings()

        var accessToken: String?
                get() {
                        val token = settings.getStringOrNull(ACCESS_TOKEN_KEY)
                        return token
                }
                set(value) {
                        if (value == null) settings.remove(ACCESS_TOKEN_KEY)
                        else settings.putString(ACCESS_TOKEN_KEY, value)
                }

        var expiresIn: Long?
                get() {
                        val expires = settings.getLongOrNull(EXPIRES_IN_KEY)
                        return expires
                }
                set(value) {
                        if (value == null) settings.remove(EXPIRES_IN_KEY)
                        else settings.putLong(EXPIRES_IN_KEY, value)
                }

        @OptIn(ExperimentalTime::class)
        internal fun isTokenValid(): Boolean {
                if (accessToken == null) {
                        return false
                }

                val expires = expiresIn
                if (expires == null) {
                        return true
                }

                return try {
                        val now = Clock.System.now().toEpochMilliseconds()
                        val isValid = now < expires
                        isValid
                } catch (e: Throwable) {
                        Log.logger.e(e) { "YandexAuthStorage: Error checking token validity" }
                        false
                }
        }

        private const val ACCESS_TOKEN_KEY = "yandex_access_token"
        private const val EXPIRES_IN_KEY = "yandex_expires_in"
}