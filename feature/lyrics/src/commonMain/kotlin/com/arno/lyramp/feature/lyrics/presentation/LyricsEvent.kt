package com.arno.lyramp.feature.lyrics.presentation

internal sealed interface LyricsEvent {
        data class WordTapped(val lineIndex: Int, val wordIndex: Int) : LyricsEvent
        data class SelectionStarted(val lineIndex: Int, val wordIndex: Int) : LyricsEvent
        data class SelectionExtended(val lineIndex: Int, val wordIndex: Int) : LyricsEvent

        object PopupDismissed : LyricsEvent
        object SaveWordRequested : LyricsEvent
        object DifficultyHighlightToggled : LyricsEvent

        object AddLyrics : LyricsEvent
        object EditLyrics : LyricsEvent
        data class UpdateLyrics(val lyrics: String) : LyricsEvent

        sealed interface Audio : LyricsEvent {
                object AudioPlayToggled : Audio
                object SlowModeToggled : Audio
        }
}
