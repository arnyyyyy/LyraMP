package com.arno.lyramp.feature.authorization.repository

interface AuthPlaylistRepository {
        fun hasPlaylist(): Boolean
        fun getPlaylistUrl(): String?
        fun savePlaylistUrl(url: String?)
}