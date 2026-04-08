package com.arno.lyramp.feature.learn_words.presentation

internal sealed interface LearnWordsUiState {
        data object Loading : LearnWordsUiState

        data class Cards(
                val words: List<WordInfo>,
                val currentIndex: Int,
                val totalCount: Int,
                val correctCount: Int,
                val incorrectCount: Int,
                val isLoadingAudio: Boolean = false
        ) : LearnWordsUiState

        data class Cram(
                val word: WordInfo,
                val userInput: String,
                val result: CheckResult?,
                val currentIndex: Int,
                val totalCount: Int,
                val correctCount: Int,
                val incorrectCount: Int
        ) : LearnWordsUiState

        data class Test(
                val word: WordInfo,
                val options: List<String>,
                val correctAnswer: String,
                val selectedIndex: Int?,
                val variant: TestVariant,
                val currentIndex: Int,
                val totalCount: Int,
                val correctCount: Int,
                val incorrectCount: Int
        ) : LearnWordsUiState {
                val isAnswered: Boolean get() = selectedIndex != null
                val isCorrect: Boolean get() = selectedIndex?.let { options[it] == correctAnswer } ?: false
        }

        data class Completed(
                val mode: LearningMode,
                val correctCount: Int,
                val incorrectCount: Int,
                val totalCount: Int
        ) : LearnWordsUiState
}
