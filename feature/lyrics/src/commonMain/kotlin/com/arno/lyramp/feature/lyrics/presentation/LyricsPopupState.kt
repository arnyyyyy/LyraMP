package com.arno.lyramp.feature.lyrics.presentation

import com.arno.lyramp.feature.translation.api.TranslationResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.update

internal sealed interface WordPopupState {
        object Hidden : WordPopupState

        data class Visible(
                val text: String,
                val lyricLine: String,
                val positions: List<WordPosition>,
                val translationResult: TranslationResult = TranslationResult(null, null),
                val isTranslating: Boolean = true,
                val audio: AudioState = AudioState(),
        ) : WordPopupState {

                val isSingleWord: Boolean
                        get() = positions.size == 1
                val anchorPosition: WordPosition
                        get() = positions.last()

                data class AudioState(
                        val isLoading: Boolean = false,
                        val isPlaying: Boolean = false,
                        val isSlowMode: Boolean = false,
                        val filePath: String? = null,
                )
        }
}

internal inline fun MutableStateFlow<WordPopupState>.updateVisible(
        block: (WordPopupState.Visible) -> WordPopupState.Visible,
) {
        update { state ->
                (state as? WordPopupState.Visible)?.let(block) ?: state
        }
}
