package com.arno.lyramp.feature.authorization.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class YandexAuthStorage(private val settings: Settings) {

        var accessToken: String?
                get() = settings.getStringOrNull(ACCESS_TOKEN_KEY)
                set(value) {
                        if (value == null) settings.remove(ACCESS_TOKEN_KEY)
                        else settings[ACCESS_TOKEN_KEY] = value
                }

        var expiresIn: Long?
                get() = settings.getLongOrNull(EXPIRES_IN_KEY)
                set(value) {
                        if (value == null) settings.remove(EXPIRES_IN_KEY)
                        else settings[EXPIRES_IN_KEY] = value
                }

        @OptIn(ExperimentalTime::class)
        fun isTokenValid(): Boolean {
                if (accessToken == null) return false
                if (expiresIn == null) return true
                return Clock.System.now().toEpochMilliseconds() < expiresIn!!
        }

        private companion object {
                const val ACCESS_TOKEN_KEY = "yandex_access_token"
                const val EXPIRES_IN_KEY = "yandex_expires_in"
        }
}
