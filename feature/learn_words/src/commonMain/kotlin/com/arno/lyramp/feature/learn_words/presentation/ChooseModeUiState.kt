package com.arno.lyramp.feature.learn_words.presentation

internal sealed interface ChooseModeUiState {
        data object Loading : ChooseModeUiState
        data object Empty : ChooseModeUiState

        data class ModeSelection(
                val words: List<WordInfo>,
        ) : ChooseModeUiState
}
