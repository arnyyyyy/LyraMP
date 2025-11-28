package com.arno.lyramp.feature.authorization.repository

import com.russhwolf.settings.Settings

internal object SpotifyAuthStorage {
        private val settings = Settings()

        var accessToken: String?
                get() = settings.getStringOrNull("access_token")
                set(value) {
                        if (value == null) settings.remove("access_token")
                        else settings.putString("access_token", value)
                }

        var refreshToken: String?
                get() = settings.getStringOrNull("refresh_token")
                set(value) {
                        if (value == null) settings.remove("refresh_token")
                        else settings.putString("refresh_token", value)
                }

        var codeVerifier: String?
                get() = settings.getStringOrNull("code_verifier")
                set(value) {
                        if (value == null) settings.remove("code_verifier")
                        else settings.putString("code_verifier", value)
                }
}