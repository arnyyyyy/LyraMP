package com.arno.lyramp.core.model

data class MusicTrack(
        val id: String? = null,
        val name: String,
        val artists: List<String>,
        val albumName: String? = null,
        val imageUrl: String? = null,
)

data class TrackInfo(
        val id: String?,
        val name: String,
        val artists: String,
        val language: String?
)
