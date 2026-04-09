package com.arno.lyramp.feature.lyrics.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.MusicTrack
import com.arno.lyramp.feature.lyrics.domain.LyricsResult
import com.arno.lyramp.feature.lyrics.domain.LyricsUseCase
import com.arno.lyramp.feature.lyrics.ui.LyricsEvent
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState.Error
import com.arno.lyramp.feature.lyrics.ui.LyricsUiState.Success
import com.arno.lyramp.feature.lyrics.ui.WordPopupState
import com.arno.lyramp.feature.translation.domain.TranslationRepository
import com.arno.lyramp.feature.translation.domain.TranslationState
import com.arno.lyramp.feature.translation.model.TranslationResult
import com.arno.lyramp.feature.translation.model.WordInfo
import com.arno.lyramp.feature.translation.speech.TranslationSpeechController
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LyricsScreenModel(
        private val track: MusicTrack,
        private val lyricsUseCase: LyricsUseCase,
        private val translationRepository: TranslationRepository,
        private val saveWordToLearn: suspend (word: String, translation: String, sourceLang: String?, trackName: String, artists: List<String>, lyricLine: String) -> Unit,
) : ScreenModel {

        private val _uiState = MutableStateFlow<LyricsUiState>(LyricsUiState.Loading)
        val uiState: StateFlow<LyricsUiState> = _uiState.asStateFlow()

        private val _popupState = MutableStateFlow<WordPopupState>(WordPopupState.Hidden)
        val popupState: StateFlow<WordPopupState> = _popupState.asStateFlow()

        private val speechController = TranslationSpeechController()

        init {
                loadLyrics()
        }

        fun loadLyrics() {
                screenModelScope.launch {
                        _uiState.value = LyricsUiState.Loading
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
                        else line.split(" ").filter { it.isNotEmpty() }
                }

        fun onEvent(event: LyricsEvent) {
                when (event) {
                        is LyricsEvent.WordTapped -> onWordTapped(event.word, event.lyricLine, event.lineIndex, event.wordIndex)
                        LyricsEvent.PopupDismissed -> dismissPopup()
                        LyricsEvent.PlayAudioRequested -> onPlayAudioRequested()
                        LyricsEvent.StopAudioRequested -> onStopAudio()
                        LyricsEvent.SlowModeToggled -> onSlowModeToggled()
                        LyricsEvent.SaveWordRequested -> onSaveWord()
                }
        }

        override fun onDispose() {
                speechController.stop()
                super.onDispose()
        }

        private fun onWordTapped(word: String, lyricLine: String, lineIndex: Int, wordIndex: Int) {
                _popupState.value = WordPopupState.Visible(
                        word = word,
                        lyricLine = lyricLine,
                        lineIndex = lineIndex,
                        wordIndex = wordIndex,
                        isTranslating = true,
                )
                screenModelScope.launch {
                        val result = translationRepository.translateWord(word)
                        _popupState.update { current ->
                                if (current !is WordPopupState.Visible
                                        || current.lineIndex != lineIndex
                                        || current.wordIndex != wordIndex
                                ) return@update current
                                when (result) {
                                        is TranslationState.Success -> current.copy(
                                                translationResult = result.translationWithLang,
                                                isTranslating = false,
                                        )

                                        is TranslationState.Error -> current.copy(
                                                translationResult = TranslationResult("Ошибка: ${result.message}", null),
                                                isTranslating = false,
                                        )

                                        else -> current.copy(
                                                translationResult = TranslationResult("Не найдено", null),
                                                isTranslating = false,
                                        )
                                }
                        }
                }
        }

        private fun dismissPopup() {
                speechController.stop()
                _popupState.value = WordPopupState.Hidden
        }

        private fun onPlayAudioRequested() {
                val current = _popupState.value as? WordPopupState.Visible ?: return
                _popupState.update { (it as? WordPopupState.Visible)?.copy(isLoadingAudio = true) ?: it }
                screenModelScope.launch {
                        val wordInfo = WordInfo(
                                word = current.word,
                                translation = current.translationResult.translation,
                                sourceLang = current.translationResult.sourceLanguage,
                        )
                        val filePath = translationRepository.getSourceSpeechFilePath(wordInfo)
                        _popupState.update { state ->
                                val visible = state as? WordPopupState.Visible ?: return@update state
                                if (filePath != null) {
                                        speechController.play(filePath) {
                                                screenModelScope.launch {
                                                        _popupState.update { s ->
                                                                (s as? WordPopupState.Visible)?.copy(isPlayingAudio = false) ?: s
                                                        }
                                                }
                                        }
                                        if (visible.isSlowMode) speechController.setPlaybackSpeed(0.7f)
                                        visible.copy(audioFilePath = filePath, isLoadingAudio = false, isPlayingAudio = true)
                                } else {
                                        visible.copy(isLoadingAudio = false, isPlayingAudio = false)
                                }
                        }
                }
        }

        private fun onStopAudio() {
                speechController.stop()
                _popupState.update { state ->
                        (state as? WordPopupState.Visible)?.copy(isPlayingAudio = false, isSlowMode = false) ?: state
                }
        }

        private fun onSlowModeToggled() {
                _popupState.update { state ->
                        val visible = state as? WordPopupState.Visible ?: return@update state
                        val newSlowMode = !visible.isSlowMode
                        if (visible.isPlayingAudio) {
                                speechController.setPlaybackSpeed(if (newSlowMode) 0.7f else 1.0f)
                        }
                        visible.copy(isSlowMode = newSlowMode)
                }
        }

        private fun onSaveWord() {
                val current = _popupState.value as? WordPopupState.Visible ?: return
                val translation = current.translationResult.translation ?: return
                screenModelScope.launch {
                        saveWordToLearn(
                                current.word,
                                translation,
                                current.translationResult.sourceLanguage,
                                track.name,
                                track.artists,
                                current.lyricLine,
                        )
                }
                dismissPopup()
        }
}
