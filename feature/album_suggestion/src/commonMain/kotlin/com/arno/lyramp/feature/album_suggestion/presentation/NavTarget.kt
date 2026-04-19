package com.arno.lyramp.feature.album_suggestion.presentation

internal sealed interface NavTarget {
        data object Loading : NavTarget
        data class ExtractionInProgress(val extracted: Int, val total: Int) : NavTarget
        data object Overview : NavTarget
        data object LoadingTrack : NavTarget
        data class ReviewWords(val trackIndex: Int, val isAlbumMode: Boolean) : NavTarget
        data class Practice(val trackIndex: Int) : NavTarget
        data class Completed(
                val trackIndex: Int,
                val trackTitle: String,
                val savedCount: Int,
                val hasNextTrack: Boolean
        ) : NavTarget

        data class Failed(val message: String) : NavTarget
}