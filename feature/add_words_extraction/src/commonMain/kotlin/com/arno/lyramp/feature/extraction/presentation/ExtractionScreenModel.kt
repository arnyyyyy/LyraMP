package com.arno.lyramp.feature.extraction.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.extraction.domain.Extractor
import com.arno.lyramp.feature.extraction.domain.WordSaver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import com.arno.lyramp.core.LanguagePreferencesRepository
import kotlinx.coroutines.CancellationException

internal class ExtractionScreenModel(
        private val extractor: Extractor,
        private val wordSaver: WordSaver,
        private val languagePreferencesRepository: LanguagePreferencesRepository,
) : ScreenModel {

        private val _uiState = MutableStateFlow<ExtractionUiState>(ExtractionUiState.Idle)
        val uiState: StateFlow<ExtractionUiState> = _uiState.asStateFlow()

        fun startExtraction() {
                if (_uiState.value is ExtractionUiState.Running) return

                _uiState.value = ExtractionUiState.Running()
                screenModelScope.launch {
                        try {
                                val languageFilter = languagePreferencesRepository.getSavedLanguage()
                                val result = extractor.extractFromRecentTracks(
                                        languageFilter = languageFilter,
                                        onProgress = { progress, trackName ->
                                                _uiState.value = ExtractionUiState.Running(
                                                        progress = progress,
                                                        currentTrack = trackName
                                                )
                                        }
                                )
                                if (result.words.isEmpty()) {
                                        _uiState.value = ExtractionUiState.Done(0)
                                } else {
                                        extractor.markAsShown(result.words)
                                        _uiState.value = ExtractionUiState.WordSelection(
                                                result = result,
                                                selectedWords = emptySet()
                                        )
                                }
                        } catch (e: CancellationException) {
                                throw e
                        } catch (e: Exception) {
                                _uiState.value = ExtractionUiState.Error(e.message ?: "Unknown error")
                        }
                }
        }

        fun toggleWord(word: String) {
                val state = _uiState.value
                if (state is ExtractionUiState.WordSelection) {
                        val newSelected = state.selectedWords.toMutableSet()
                        if (word in newSelected) newSelected.remove(word) else newSelected.add(word)
                        _uiState.value = state.copy(selectedWords = newSelected)
                }
        }

        fun toggleSelectAll() {
                val state = _uiState.value
                if (state is ExtractionUiState.WordSelection) {
                        val allSelected = state.selectedWords.size == state.result.words.size
                        _uiState.value = state.copy(
                                selectedWords = if (allSelected) emptySet()
                                else state.result.words.map { it.word }.toSet()
                        )
                }
        }

        fun saveSelectedWords() {
                val state = _uiState.value
                if (state !is ExtractionUiState.WordSelection) return

                val wordsToSave = state.result.words.filter { it.word in state.selectedWords }
                if (wordsToSave.isEmpty()) {
                        _uiState.value = ExtractionUiState.Done(0)
                        return
                }

                _uiState.value = ExtractionUiState.Saving
                screenModelScope.launch {
                        try {
                                val savedCount = wordSaver.saveAll(wordsToSave)
                                _uiState.value = ExtractionUiState.Done(savedCount)
                        } catch (e: CancellationException) {
                                throw e
                        } catch (e: Exception) {
                                _uiState.value = ExtractionUiState.Error(e.message ?: "Ошибка сохранения")
                        }
                }
        }
}
