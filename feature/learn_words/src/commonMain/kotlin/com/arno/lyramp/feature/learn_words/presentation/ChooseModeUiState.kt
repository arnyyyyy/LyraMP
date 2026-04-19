package com.arno.lyramp.feature.learn_words.presentation

import com.arno.lyramp.core.model.CefrDifficultyGroup

internal sealed interface ChooseModeUiState {
        data object Loading : ChooseModeUiState
        data object Empty : ChooseModeUiState

        data class ModeSelection(
                val words: List<WordInfo>,
                val cefrGroups: Map<CefrDifficultyGroup, Int>? = null
        ) : ChooseModeUiState
}
