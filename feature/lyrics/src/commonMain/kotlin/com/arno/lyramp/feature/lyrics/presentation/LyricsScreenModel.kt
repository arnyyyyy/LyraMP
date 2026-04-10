package com.arno.lyramp.feature.lyrics.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.core.model.MusicTrack
import com.arno.lyramp.core.model.WordDifficultyProvider
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import com.arno.lyramp.feature.lyrics.domain.LyricsUseCase
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState.Loading
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState.Success
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState.Error
import com.arno.lyramp.feature.translation.domain.TranslateWordWithStateUseCase
import com.arno.lyramp.feature.translation.domain.TranslationState
import com.arno.lyramp.feature.translation.model.TranslationResult
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class LyricsScreenModel(
        private val track: MusicTrack,
        private val lyricsUseCase: LyricsUseCase,
        private val translateWord: TranslateWordWithStateUseCase,
        private val audioManager: PopupAudioManager,
        private val saveWordToLearn: suspend (word: String, translation: String, sourceLang: String?, trackName: String, artists: List<String>, lyricLine: String) -> Unit,
        private val wordDifficultyProvider: WordDifficultyProvider? = null,
        getSelectedLanguage: GetSelectedLanguageUseCase? = null,
) : ScreenModel {

        private val _uiState = MutableStateFlow<LyricsUiState>(LyricsUiState.Loading)
        val uiState: StateFlow<LyricsUiState> = _uiState.asStateFlow()

        private val _popupState = MutableStateFlow<WordPopupState>(WordPopupState.Hidden)
        val popupState: StateFlow<WordPopupState> = _popupState.asStateFlow()

        private val _selectionState = MutableStateFlow(SelectionState())
        val selectionState: StateFlow<SelectionState> = _selectionState.asStateFlow()

        val selectedLanguage: String? = getSelectedLanguage?.invoke()

        private val _highlightEnabled = MutableStateFlow(false)
        val highlightEnabled: StateFlow<Boolean> = _highlightEnabled.asStateFlow()

        private val _wordLevels = MutableStateFlow<Map<String, CefrLevel>>(emptyMap())
        val wordLevels: StateFlow<Map<String, CefrLevel>> = _wordLevels.asStateFlow()

        val canShowDifficultyButton: Boolean
                get() = selectedLanguage != null && wordDifficultyProvider != null

        init {
                loadLyrics()
        }

        fun loadLyrics() {
                screenModelScope.launch {
                        _uiState.value = Loading
                        try {
                                when (val result = lyricsUseCase.getLyrics(
                                        track.artists.joinToString(", "),
                                        track.name,
                                        track.id
                                )) {
                                        is LyricsResult.Found -> _uiState.value = Success(parseLyricsLines(result.lyrics))
                                        LyricsResult.NotFound -> _uiState.value = Error("Текст песни не найден")
                                }
                        } catch (e: Throwable) {
                                _uiState.value = Error(e.message ?: "Unknown error")
                        }
                }
        }

        private fun parseLyricsLines(lyrics: String): List<List<String>> =
                lyrics.split("\n").map { line ->
                        if (line.isBlank()) emptyList()
                        else line.split(Regex("\\s+")).filter { it.isNotEmpty() }
                }

        fun onEvent(event: LyricsEvent) {
                when (event) {
                        is LyricsEvent.WordTapped -> onWordTapped(event.lineIndex, event.wordIndex)
                        is LyricsEvent.SelectionStarted -> onSelectionStarted(event.lineIndex, event.wordIndex)
                        is LyricsEvent.SelectionExtended -> onSelectionExtended(event.lineIndex, event.wordIndex)
                        LyricsEvent.PopupDismissed -> dismissPopup()
                        LyricsEvent.Audio.AudioPlayToggled -> onTogglePlay()
                        LyricsEvent.Audio.SlowModeToggled -> audioManager.toggleSlowMode(_popupState)
                        LyricsEvent.SaveWordRequested -> onSaveWord()
                        LyricsEvent.DifficultyHighlightToggled -> toggleDifficultyHighlight()
                }
        }

        private fun toggleDifficultyHighlight() {
                val newState = !_highlightEnabled.value
                _highlightEnabled.value = newState
                if (newState && _wordLevels.value.isEmpty()) {
                        loadWordDifficulties()
                }
        }

        private fun loadWordDifficulties() {
                val provider = wordDifficultyProvider ?: return
                val lang = selectedLanguage ?: return
                screenModelScope.launch {
                        _wordLevels.value = provider.getWordLevels(lang)
                }
        }

        private fun currentLines(): List<List<String>> =
                (_uiState.value as? Success)?.lyricsLines ?: emptyList()

        private fun wordAt(pos: WordPosition): String? =
                currentLines().getOrNull(pos.lineIndex)?.getOrNull(pos.wordIndex)

        private fun lyricLineAt(lineIndex: Int): String =
                currentLines().getOrNull(lineIndex)?.joinToString(" ") ?: ""

        private fun onWordTapped(lineIndex: Int, wordIndex: Int) {
                val word = wordAt(WordPosition(lineIndex, wordIndex)) ?: return
                val pos = WordPosition(lineIndex, wordIndex)
                showPopupAndTranslate(word, listOf(pos), lyricLineAt(lineIndex))
        }

        private fun onSelectionStarted(lineIndex: Int, wordIndex: Int) {
                dismissPopup()
                _selectionState.value = SelectionState(anchor = WordPosition(lineIndex, wordIndex))
        }

        private fun onSelectionExtended(lineIndex: Int, wordIndex: Int) {
                val anchor = _selectionState.value.anchor ?: return
                val end = WordPosition(lineIndex, wordIndex)

                if (anchor == end) {
                        dismissPopup()
                        return
                }

                _selectionState.value = SelectionState(anchor = anchor, end = end)

                val positions = _selectionState.value.getSelectedRange(currentLines())
                val combinedText = positions.mapNotNull { wordAt(it) }.joinToString(" ")
                showPopupAndTranslate(combinedText, positions, lyricLineAt(anchor.lineIndex))
        }

        private fun showPopupAndTranslate(
                text: String,
                positions: List<WordPosition>,
                lyricLine: String,
        ) {
                _popupState.value = WordPopupState.Visible(
                        text = text,
                        lyricLine = lyricLine,
                        positions = positions,
                        isTranslating = true,
                )
                screenModelScope.launch {
                        val result = translateWord(text)
                        _popupState.updateVisible { visible ->
                                if (visible.text != text) return@updateVisible visible
                                when (result) {
                                        is TranslationState.Success -> visible.copy(
                                                translationResult = result.translationWithLang,
                                                isTranslating = false,
                                        )

                                        is TranslationState.Error -> visible.copy(
                                                translationResult = TranslationResult("Ошибка: ${result.message}", null),
                                                isTranslating = false,
                                        )

                                        else -> visible.copy(
                                                translationResult = TranslationResult("Не найдено", null),
                                                isTranslating = false,
                                        )
                                }
                        }
                }
        }

        private fun dismissPopup() {
                audioManager.stop()
                _popupState.value = WordPopupState.Hidden
                _selectionState.value = SelectionState()
        }

        private fun onSaveWord() {
                val current = _popupState.value as? WordPopupState.Visible ?: return
                val translation = current.translationResult.translation ?: return
                screenModelScope.launch {
                        saveWordToLearn(
                                current.text,
                                translation,
                                current.translationResult.sourceLanguage,
                                track.name,
                                track.artists,
                                current.lyricLine,
                        )
                }
                dismissPopup()
        }

        private fun onTogglePlay() {
                val current = _popupState.value as? WordPopupState.Visible ?: return
                screenModelScope.launch {
                        audioManager.togglePlay(_popupState, current) {
                                screenModelScope.launch {
                                        _popupState.updateVisible { it.copy(audio = it.audio.copy(isPlaying = false)) }
                                }
                        }
                }
        }
}
