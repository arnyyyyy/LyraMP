package com.arno.lyramp.feature.authorization.repository

import com.russhwolf.settings.Settings

internal object AppleAuthStorage {
        private val settings = Settings()

        var playlistUrl: String?
                get() = settings.getStringOrNull(PLAYLIST_URL_KEY)
                set(value) {
                        if (value == null) settings.remove(PLAYLIST_URL_KEY)
                        else settings.putString(PLAYLIST_URL_KEY, value)
                }

        private const val PLAYLIST_URL_KEY = "apple_playlist_url"
}

