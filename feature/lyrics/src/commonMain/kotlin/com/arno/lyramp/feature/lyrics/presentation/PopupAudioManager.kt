package com.arno.lyramp.feature.lyrics.presentation

import com.arno.lyramp.feature.translation.domain.GetSpeechFilePathUseCase
import com.arno.lyramp.feature.translation.domain.WordInfo
import com.arno.lyramp.feature.translation.speech.TranslationSpeechController
import kotlinx.coroutines.flow.MutableStateFlow

internal class PopupAudioManager(
        private val getSpeechFilePath: GetSpeechFilePathUseCase,
        private val speechController: TranslationSpeechController,
) {

        internal fun stop() {
                speechController.stop()
        }

        internal suspend fun togglePlay(
                popupState: MutableStateFlow<WordPopupState>,
                current: WordPopupState.Visible,
                onPlaybackFinished: () -> Unit,
        ) {
                if (current.audio.isPlaying) {
                        speechController.stop()
                        popupState.updateVisible { it.copy(audio = it.audio.copy(isPlaying = false, isSlowMode = false)) }
                } else {
                        popupState.updateVisible { it.copy(audio = it.audio.copy(isLoading = true)) }

                        val wordInfo = WordInfo(
                                word = current.text,
                                translation = current.translationResult.translation,
                                sourceLang = current.translationResult.sourceLanguage,
                        )
                        val filePath = getSpeechFilePath(wordInfo)

                        popupState.updateVisible { visible ->
                                if (filePath != null) {
                                        speechController.play(filePath, onPlaybackFinished)
                                        if (visible.audio.isSlowMode) speechController.setPlaybackSpeed(0.7f)
                                        visible.copy(audio = visible.audio.copy(filePath = filePath, isLoading = false, isPlaying = true))
                                } else {
                                        visible.copy(audio = visible.audio.copy(isLoading = false))
                                }
                        }
                }
        }

        internal fun toggleSlowMode(popupState: MutableStateFlow<WordPopupState>) {
                popupState.updateVisible { visible ->
                        val newSlowMode = !visible.audio.isSlowMode
                        if (visible.audio.isPlaying) {
                                speechController.setPlaybackSpeed(if (newSlowMode) 0.7f else 1.0f)
                        }
                        visible.copy(audio = visible.audio.copy(isSlowMode = newSlowMode))
                }
        }
}
