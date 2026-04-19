package com.arno.lyramp.feature.album_suggestion.presentation

import com.arno.lyramp.feature.album_suggestion.domain.model.SuggestedWord

internal sealed interface AlbumSuggestionUiState {

        data object Loading : AlbumSuggestionUiState

        data class ExtractionProgress(
                val extracted: Int,
                val total: Int
        ) : AlbumSuggestionUiState

        data class AlbumOverview(
                val albumId: String,
                val albumTitle: String,
                val artistName: String,
                val coverUri: String?,
                val tracks: List<TrackStats>,
                val totalWords: Int,
                val learnedWords: Int,
                val levelLabel: String
        ) : AlbumSuggestionUiState {
                val wordsToLearn: Int get() = totalWords - learnedWords

                val progressPercent: Int
                        get() = if (totalWords > 0) (learnedWords * 100 / totalWords) else 0

                val progressFraction: Float
                        get() = if (totalWords > 0) learnedWords.toFloat() / totalWords else 0f
        }

        data object LoadingTrackWords : AlbumSuggestionUiState

        data class TrackWordsList(
                val trackIndex: Int,
                val trackTitle: String,
                val words: List<SuggestedWord>,
                val knownWords: Set<String> = emptySet(),
                val isAlbumMode: Boolean = false
        ) : AlbumSuggestionUiState

        data class TrackPractice(
                val albumId: String,
                val trackIndex: Int,
                val trackTitle: String,
                val words: List<PracticeWord>,
                val totalInTrack: Int,
                val learnedInTrack: Int
        ) : AlbumSuggestionUiState

        data class LevelCompleted(
                val trackIndex: Int,
                val trackTitle: String,
                val savedCount: Int,
                val hasNextTrack: Boolean = false
        ) : AlbumSuggestionUiState

        data class Error(val message: String) : AlbumSuggestionUiState
}

internal data class TrackStats(
        val trackIndex: Int,
        val title: String,
        val totalWords: Int,
        val learnedWords: Int,
        val pendingWords: Int = 0
) {
        val progressFraction: Float
                get() = if (totalWords > 0) learnedWords.toFloat() / totalWords else 0f

        val isCompleted: Boolean
                get() = totalWords > 0 && pendingWords == 0 && learnedWords >= totalWords
}

internal data class PracticeWord(
        val id: Long,
        val word: String,
        val translation: String,
        val lyricLine: String,
        val progress: Float
)
