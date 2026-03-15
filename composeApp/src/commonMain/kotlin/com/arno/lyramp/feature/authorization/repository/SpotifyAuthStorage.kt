package com.arno.lyramp.feature.authorization.repository

import com.russhwolf.settings.Settings
import com.russhwolf.settings.set

internal class SpotifyAuthStorage(private val settings: Settings) {

        var accessToken: String?
                get() = settings.getStringOrNull(ACCESS_TOKEN_KEY)
                set(value) {
                        if (value == null) settings.remove(ACCESS_TOKEN_KEY)
                        else settings[ACCESS_TOKEN_KEY] = value
                }

        var refreshToken: String?
                get() = settings.getStringOrNull(REFRESH_TOKEN_KEY)
                set(value) {
                        if (value == null) settings.remove(REFRESH_TOKEN_KEY)
                        else settings[REFRESH_TOKEN_KEY] = value
                }

        var codeVerifier: String?
                get() = settings.getStringOrNull(CODE_VERIFIER_KEY)
                set(value) {
                        if (value == null) settings.remove(CODE_VERIFIER_KEY)
                        else settings[CODE_VERIFIER_KEY] = value
                }

        private companion object {
                const val ACCESS_TOKEN_KEY = "spotify_access_token"
                const val REFRESH_TOKEN_KEY = "spotify_refresh_token"
                const val CODE_VERIFIER_KEY = "spotify_code_verifier"
        }
}
