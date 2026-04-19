package com.arno.lyramp.feature.album_suggestion.presentation

internal data class AlbumSelectorItem(
        val albumId: String,
        val albumTitle: String,
        val artistName: String,
        val imageUrl: String?,
        val totalTracks: Int? = null
)
