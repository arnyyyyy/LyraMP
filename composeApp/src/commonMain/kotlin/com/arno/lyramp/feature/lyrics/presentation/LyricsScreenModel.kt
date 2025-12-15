package com.arno.lyramp.feature.lyrics.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import com.arno.lyramp.feature.lyrics.repository.LyricsGetterRepository
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState.Error
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState.Success
import com.arno.lyramp.feature.translation.presentation.TranslationState
import com.arno.lyramp.feature.translation.repository.TranslationRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class LyricsScreenModel(
        private val artist: String,
        private val song: String,
        private val lyricsRepository: LyricsGetterRepository,
) : ScreenModel {
        private val scope: CoroutineScope = MainScope()

        private val _uiState = MutableStateFlow<LyricsUiState>(LyricsUiState.Loading)
        val uiState: StateFlow<LyricsUiState> = _uiState.asStateFlow()

        init {
                loadLyrics()
        }

        fun loadLyrics() {
                scope.launch {
                        _uiState.value = LyricsUiState.Loading
                        try {
                                val result = lyricsRepository.searchLyrics(artist, song)
                                when (result) {
                                        is LyricsState.Success -> {
                                                val lyrics = result.lyrics.firstOrNull()?.lyrics ?: ""
                                                _uiState.value = Success(lyrics)
                                        }

                                        is LyricsState.Error -> {
                                                _uiState.value = Error(result.message)
                                        }

                                        else -> {
                                                _uiState.value = Error("Не удалось получить текст песни")
                                        }
                                }
                        } catch (e: Throwable) {
                                _uiState.value = Error(e.message ?: "Unknown error")
                        }
                }
        }

        override fun onDispose() {
                scope.cancel()
        }
}

