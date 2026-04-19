package com.arno.lyramp.feature.album_suggestion.data

data class AlbumProgressInfo(
        val albumId: String,
        val albumTitle: String,
        val artistName: String,
        val coverUri: String?,
        val totalTracks: Int,
        val wordsExtracted: Boolean = false,
        val extractedLanguage: String? = null,
        val extractedLevels: String? = null
)