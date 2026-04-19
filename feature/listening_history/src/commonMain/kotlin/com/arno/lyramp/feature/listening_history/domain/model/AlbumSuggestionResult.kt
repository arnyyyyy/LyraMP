package com.arno.lyramp.feature.listening_history.domain.model

data class AlbumSuggestionResult(
        val albumId: String,
        val albumTitle: String,
        val artistName: String,
        val imageUrl: String?,
        val likedTrackCount: Int
)
