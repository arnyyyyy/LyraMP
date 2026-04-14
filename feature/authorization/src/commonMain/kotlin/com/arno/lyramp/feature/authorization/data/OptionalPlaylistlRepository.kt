package com.arno.lyramp.feature.authorization.data

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.russhwolf.settings.Settings

internal class OptionalPlaylistRepository : AuthPlaylistRepository {
        override fun hasPlaylist(): Boolean = !OptionalPlaylistStorage.playlistUrl.isNullOrBlank()
        override fun getPlaylistUrl(): String? = OptionalPlaylistStorage.playlistUrl
        override fun savePlaylistUrl(url: String?) {
                OptionalPlaylistStorage.playlistUrl = url
                val current = AuthSelectionStorage.lastAuthorizedService
                val isOAuthAuthorized = current in listOf(
                        MusicServiceType.YANDEX.name,
                        MusicServiceType.APPLE.name,
                )
                if (!isOAuthAuthorized) {
                        AuthSelectionStorage.lastAuthorizedService = MusicServiceType.NONE.name
                }
        }
}

private object OptionalPlaylistStorage {
        private val settings = Settings()

        var playlistUrl: String?
                get() = settings.getStringOrNull(PLAYLIST_URL_KEY)
                set(value) {
                        if (value == null) settings.remove(PLAYLIST_URL_KEY)
                        else settings.putString(PLAYLIST_URL_KEY, value)
                }

        private const val PLAYLIST_URL_KEY = "generic_playlist_url"
}
