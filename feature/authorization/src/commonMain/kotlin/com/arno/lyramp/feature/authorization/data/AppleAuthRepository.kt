package com.arno.lyramp.feature.authorization.data

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.russhwolf.settings.Settings

internal class AppleAuthRepository : AuthPlaylistRepository {
        override fun hasPlaylist(): Boolean = !AppleAuthStorage.playlistUrl.isNullOrBlank()
        override fun getPlaylistUrl(): String? = AppleAuthStorage.playlistUrl
        override fun savePlaylistUrl(url: String?) {
                AppleAuthStorage.playlistUrl = url
                AuthSelectionStorage.lastAuthorizedService = if (url.isNullOrBlank()) null else MusicServiceType.APPLE.name
        }
}

private object AppleAuthStorage {
        private val settings = Settings()

        var playlistUrl: String?
                get() = settings.getStringOrNull(PLAYLIST_URL_KEY)
                set(value) {
                        if (value == null) settings.remove(PLAYLIST_URL_KEY)
                        else settings.putString(PLAYLIST_URL_KEY, value)
                }

        private const val PLAYLIST_URL_KEY = "apple_playlist_url"
}
