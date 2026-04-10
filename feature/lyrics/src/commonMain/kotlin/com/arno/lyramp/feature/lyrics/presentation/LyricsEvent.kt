package com.arno.lyramp.feature.lyrics.presentation

sealed interface LyricsEvent {
        data class WordTapped(val lineIndex: Int, val wordIndex: Int) : LyricsEvent
        data class SelectionStarted(val lineIndex: Int, val wordIndex: Int) : LyricsEvent
        data class SelectionExtended(val lineIndex: Int, val wordIndex: Int) : LyricsEvent

        object PopupDismissed : LyricsEvent
        object SaveWordRequested : LyricsEvent

        sealed interface Audio : LyricsEvent {
                object AudioPlayToggled : Audio
                object SlowModeToggled : Audio
        }
}
