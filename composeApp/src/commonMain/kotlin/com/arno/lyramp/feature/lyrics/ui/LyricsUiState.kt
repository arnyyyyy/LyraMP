package com.arno.lyramp.feature.lyrics.ui

import com.arno.lyramp.feature.translation.model.TranslationResult

internal sealed interface LyricsUiState {
        object Loading : LyricsUiState
        data class Success(val lyricsLines: List<List<String>>) : LyricsUiState
        data class Error(val message: String) : LyricsUiState
}

internal sealed interface WordPopupState {
        object Hidden : WordPopupState
        data class Visible(
                val word: String,
                val lyricLine: String,
                val lineIndex: Int,
                val wordIndex: Int,
                val translationResult: TranslationResult = TranslationResult(null, null),
                val isTranslating: Boolean = true,
                val audioFilePath: String? = null,
                val isLoadingAudio: Boolean = false,
                val isPlayingAudio: Boolean = false,
                val isSlowMode: Boolean = false,
        ) : WordPopupState
}

internal sealed interface LyricsEvent {
        data class WordTapped(
                val word: String,
                val lyricLine: String,
                val lineIndex: Int,
                val wordIndex: Int,
        ) : LyricsEvent

        object PopupDismissed : LyricsEvent
        object PlayAudioRequested : LyricsEvent
        object StopAudioRequested : LyricsEvent
        object SlowModeToggled : LyricsEvent
        object SaveWordRequested : LyricsEvent
}
