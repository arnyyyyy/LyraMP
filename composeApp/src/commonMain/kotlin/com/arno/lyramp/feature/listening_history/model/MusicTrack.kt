package com.arno.lyramp.feature.listening_history.model

data class MusicTrack(
        val id: String? = null,
        val albumId: String? = null,
        val name: String,
        val artists: List<String>,
        val albumName: String? = null,
        val imageUrl: String? = null,
        val hasText: Boolean = false
)
