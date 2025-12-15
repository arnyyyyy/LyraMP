package com.arno.lyramp.feature.authorization.repository

import com.russhwolf.settings.Settings

internal object YandexAuthStorage {
        private val settings = Settings()

        var playlistUrl: String?
                get() = settings.getStringOrNull("yandex_playlist_url")
                set(value) {
                        if (value == null) settings.remove("yandex_playlist_url")
                        else settings.putString("yandex_playlist_url", value)
                }
}

