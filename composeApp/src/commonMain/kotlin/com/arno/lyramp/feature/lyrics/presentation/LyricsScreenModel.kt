package com.arno.lyramp.feature.lyrics.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.listening_history.model.MusicTrack
import com.arno.lyramp.feature.lyrics.domain.LyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState.Error
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState.Success
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class LyricsScreenModel(
        private val track: MusicTrack,
        private val lyricsUseCase: LyricsUseCase,
) : ScreenModel {
        private val _uiState = MutableStateFlow<LyricsUiState>(LyricsUiState.Loading)
        val uiState: StateFlow<LyricsUiState> = _uiState.asStateFlow()

        init {
                loadLyrics()
        }

        fun loadLyrics() {
                screenModelScope.launch {
                        _uiState.value = LyricsUiState.Loading
                        try {
                                when (val result = lyricsUseCase.getLyrics(track.artists.joinToString(", "), track.name, track.id)) {
                                        is LyricsResult.Found ->
                                                _uiState.value = Success(result.lyrics)

                                        LyricsResult.NotFound ->
                                                _uiState.value = Error("Текст песни не найден")

                                }
                        } catch (e: Throwable) {
                                _uiState.value = Error(e.message ?: "Unknown error")
                        }
                }
        }
}
