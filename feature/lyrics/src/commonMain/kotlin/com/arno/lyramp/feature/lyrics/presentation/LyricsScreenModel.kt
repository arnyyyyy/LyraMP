package com.arno.lyramp.feature.lyrics.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.core.model.MusicTrack
import com.arno.lyramp.core.model.WordDifficultyProvider
import com.arno.lyramp.feature.lyrics.data.CustomLyricsRepository
import com.arno.lyramp.feature.lyrics.domain.GetLyricsUseCase
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import com.arno.lyramp.feature.lyrics.domain.LyricsTextParser
import com.arno.lyramp.feature.lyrics.domain.SaveWordToLearnUseCase
import com.arno.lyramp.feature.lyrics.presentation.LyricsUiState.Loading
import com.arno.lyramp.feature.lyrics.presentation.LyricsUiState.Success
import com.arno.lyramp.feature.lyrics.presentation.LyricsUiState.Error
import com.arno.lyramp.feature.translation.api.TranslationResult
import com.arno.lyramp.feature.translation.domain.TranslateWordUseCase
import com.arno.lyramp.feature.translation.domain.TranslationState
import com.arno.lyramp.feature.translation.domain.displayText
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class LyricsScreenModel(
        private val track: MusicTrack,
        private val getLyrics: GetLyricsUseCase,
        private val customLyricsRepository: CustomLyricsRepository,
        private val translateWord: TranslateWordUseCase,
        private val audioManager: PopupAudioManager,
        private val saveWordToLearn: SaveWordToLearnUseCase,
        private val lyricsTextParser: LyricsTextParser,
        private val wordDifficultyProvider: WordDifficultyProvider? = null,
        getSelectedLanguage: GetSelectedLanguageUseCase? = null,
) : ScreenModel {

        private val _uiState = MutableStateFlow<LyricsUiState>(Loading)
        val uiState: StateFlow<LyricsUiState> = _uiState.asStateFlow()

        private val _popupState = MutableStateFlow<WordPopupState>(WordPopupState.Hidden)
        val popupState: StateFlow<WordPopupState> = _popupState.asStateFlow()

        private val _selectionState = MutableStateFlow(SelectionState())
        val selectionState: StateFlow<SelectionState> = _selectionState.asStateFlow()

        val selectedLanguage: String? = getSelectedLanguage?.invoke()

        private val _highlightEnabled = MutableStateFlow(false)
        val highlightEnabled: StateFlow<Boolean> = _highlightEnabled.asStateFlow()

        private val _wordSaved = MutableSharedFlow<Unit>(extraBufferCapacity = 1)
        val wordSaved: SharedFlow<Unit> = _wordSaved

        private val _wordLevels = MutableStateFlow<Map<String, CefrLevel>>(emptyMap())
        val wordLevels: StateFlow<Map<String, CefrLevel>> = _wordLevels.asStateFlow()

        val canShowDifficultyButton = selectedLanguage != null && wordDifficultyProvider != null

        init {
                loadLyrics()
        }

        private fun loadLyrics() {
                screenModelScope.launch {
                        _uiState.value = Loading
                        try {
                                val artist = track.artists.joinToString(", ")
                                val song = track.name

                                val cached = customLyricsRepository.getCustomLyrics(artist, song)
                                if (!cached.isNullOrBlank()) {
                                        _uiState.value = Success(lyricsTextParser.parse(cached))
                                        return@launch
                                }

                                when (val result = getLyrics(artist, song, track.id)) {
                                        is LyricsResult.Found -> _uiState.value = Success(lyricsTextParser.parse(result.lyrics))
                                        LyricsResult.NotFound -> _uiState.value = Error("Текст песни не найден")
                                }
                        } catch (e: CancellationException) {
                                throw e
                        } catch (e: Throwable) {
                                _uiState.value = Error(e.message ?: "Unknown error")
                        }
                }
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
                        LyricsEvent.AddLyrics -> _uiState.value = LyricsUiState.Editing()
                        LyricsEvent.EditLyrics -> onEditLyricsRequested()
                        is LyricsEvent.UpdateLyrics -> onManualLyricsSubmitted(event.lyrics)
                }
        }

        private fun onManualLyricsSubmitted(lyrics: String) {
                if (lyrics.isBlank()) return
                screenModelScope.launch {
                        customLyricsRepository.saveCustomLyrics(
                                artist = track.artists.joinToString(", "),
                                song = track.name,
                                lyrics = lyrics
                        )
                        _uiState.value = Success(lyricsTextParser.parse(lyrics))
                }
        }

        private fun onEditLyricsRequested() {
                val current = (_uiState.value as? Success) ?: return
                val lyricsText = current.lyricsLines.joinToString("\n") { it.joinToString(" ") }
                _uiState.value = LyricsUiState.Editing(initialText = lyricsText)
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
                                                translationResult = TranslationResult(result.displayText(), null),
                                                isTranslating = false,
                                        )

                                        else -> visible.copy(
                                                translationResult = TranslationResult(result.displayText(), null),
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
                                word = current.text,
                                translation = translation,
                                sourceLang = current.translationResult.sourceLanguage,
                                trackName = track.name,
                                artists = track.artists,
                                lyricLine = current.lyricLine,
                        )
                        _wordSaved.emit(Unit)
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

        override fun onDispose() {
                audioManager.stop()
                super.onDispose()
        }
}
