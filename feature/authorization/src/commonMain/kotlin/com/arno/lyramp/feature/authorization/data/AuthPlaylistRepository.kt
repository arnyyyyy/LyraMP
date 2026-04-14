package com.arno.lyramp.feature.authorization.data

internal interface AuthPlaylistRepository {
        fun hasPlaylist(): Boolean
        fun getPlaylistUrl(): String?
        fun savePlaylistUrl(url: String?)
}
