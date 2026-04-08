package com.arno.lyramp.feature.authorization.repository

import com.arno.lyramp.feature.authorization.model.MusicServiceType

class AppleAuthRepository : AuthPlaylistRepository {
        override fun hasPlaylist(): Boolean = !AppleAuthStorage.playlistUrl.isNullOrBlank()
        override fun getPlaylistUrl(): String? = AppleAuthStorage.playlistUrl
        override fun savePlaylistUrl(url: String?) {
                AppleAuthStorage.playlistUrl = url
                AuthSelectionStorage.lastAuthorizedService = if (url.isNullOrBlank()) null else MusicServiceType.APPLE.name
        }
}
