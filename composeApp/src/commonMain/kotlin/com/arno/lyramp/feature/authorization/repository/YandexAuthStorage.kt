package com.arno.lyramp.feature.authorization.repository

import com.russhwolf.settings.Settings
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal object YandexAuthStorage {
        private val settings = Settings()

        var accessToken: String?
                get() = settings.getStringOrNull(ACCESS_TOKEN_KEY)
                set(value) {
                        if (value == null) settings.remove(ACCESS_TOKEN_KEY)
                        else settings.putString(ACCESS_TOKEN_KEY, value)
                }

        var expiresIn: Long?
                get() = settings.getLongOrNull(EXPIRES_IN_KEY)
                set(value) {
                        if (value == null) settings.remove(EXPIRES_IN_KEY)
                        else settings.putLong(EXPIRES_IN_KEY, value)
                }

        @OptIn(ExperimentalTime::class)
        internal fun isTokenValid(): Boolean {
                if (accessToken == null) return false
                if (expiresIn == null) return true

                val now = Clock.System.now().toEpochMilliseconds()
                return now < expiresIn!!
        }

        private const val ACCESS_TOKEN_KEY = "yandex_access_token"
        private const val EXPIRES_IN_KEY = "yandex_expires_in"
}