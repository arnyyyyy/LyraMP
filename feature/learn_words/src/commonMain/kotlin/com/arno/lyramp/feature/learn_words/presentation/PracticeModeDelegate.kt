package com.arno.lyramp.feature.learn_words.presentation

import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import kotlin.math.roundToInt

internal enum class PracticeSessionPlan {
        CRAM_ONLY,
        MIXED_CRAM_TEST,
        TEST_ONLY,
}

internal class PracticeModeDelegate(
        private val mode: LearningMode,
        private val sessionPlan: PracticeSessionPlan,
        private val shuffledWords: List<LearnWordEntity>,
        private val allFilteredWords: List<LearnWordEntity>,
        private val repository: LearnWordsRepository,
        private val uiState: MutableStateFlow<LearnWordsUiState>,
        private val coroutineScope: CoroutineScope,
) {
        private enum class TaskType { CRAM, TEST }

        private val taskTypes = buildTaskTypes(shuffledWords.size)
        private var correctCount = 0
        private var incorrectCount = 0
        private var currentCramHintUsed = false

        init {
                emitState(0)
        }

        fun onInputChange(input: String) {
                val state = uiState.value as? LearnWordsUiState.Cram ?: return
                uiState.value = state.copy(userInput = input, result = null)
        }

        fun onHintShown() {
                if (uiState.value is LearnWordsUiState.Cram) {
                        currentCramHintUsed = true
                }
        }

        fun onSkip() {
                val state = uiState.value as? LearnWordsUiState.Cram ?: return
                if (state.result != null) return
                incorrectCount++

                uiState.value = state.copy(
                        result = CheckResult.INCORRECT,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount,
                        lastCheckedWord = state.word.word
                )
        }

        fun onCheck() {
                val state = uiState.value as? LearnWordsUiState.Cram ?: return
                if (state.result != null) return

                val correct = state.word.word.trim().equals(state.userInput.trim(), ignoreCase = true)
                if (correct) correctCount++ else incorrectCount++

                uiState.value = state.copy(
                        result = if (correct) CheckResult.CORRECT else CheckResult.INCORRECT,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount,
                        lastCheckedWord = state.word.word
                )
                if (correct) {
                        val progressStep = if (currentCramHintUsed) CRAM_HINT_PROGRESS_STEP else CRAM_PROGRESS_STEP
                        coroutineScope.launch { repository.incrementProgress(state.word.id, progressStep) }
                }
        }

        fun onSelectOption(index: Int) {
                val state = uiState.value as? LearnWordsUiState.Test ?: return
                if (state.isAnswered) return

                val correct = state.options[index] == state.correctAnswer
                if (correct) correctCount++ else incorrectCount++

                uiState.value = state.copy(
                        selectedIndex = index,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount
                )
                if (correct) {
                        coroutineScope.launch { repository.incrementProgress(state.word.id, TEST_PROGRESS_STEP) }
                }
        }

        fun onNext() {
                val state = uiState.value
                val currentIndex = when (state) {
                        is LearnWordsUiState.Cram -> state.currentIndex
                        is LearnWordsUiState.Test -> state.currentIndex
                        else -> return
                }

                val next = currentIndex + 1
                if (next >= shuffledWords.size) {
                        uiState.value = LearnWordsUiState.Completed(
                                mode = mode,
                                correctCount = correctCount,
                                incorrectCount = incorrectCount,
                                totalCount = shuffledWords.size
                        )
                } else {
                        emitState(next)
                }
        }

        private fun emitState(index: Int) {
                currentCramHintUsed = false
                when (taskTypes[index]) {
                        TaskType.CRAM -> emitCramState(index)
                        TaskType.TEST -> emitTestState(index)
                }
        }

        private fun emitCramState(index: Int) {
                val word = shuffledWords[index]
                uiState.value = LearnWordsUiState.Cram(
                        word = word.toDomain(),
                        userInput = "",
                        result = null,
                        currentIndex = index,
                        totalCount = shuffledWords.size,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount,
                        lastCheckedWord = null
                )
        }

        private fun emitTestState(index: Int) {
                val word = shuffledWords[index]
                val variant = TestVariant.entries.random()
                val others = allFilteredWords.filter { it.id != word.id }.shuffled()

                val (correctAnswer, wrongAnswers) = when (variant) {
                        TestVariant.FOREIGN_TO_TRANSLATION ->
                                word.translation to others.take(3).map { it.translation }

                        TestVariant.TRANSLATION_TO_FOREIGN ->
                                word.word to others.take(3).map { it.word }
                }

                uiState.value = LearnWordsUiState.Test(
                        word = word.toDomain(),
                        options = (wrongAnswers + correctAnswer).shuffled(),
                        correctAnswer = correctAnswer,
                        selectedIndex = null,
                        variant = variant,
                        currentIndex = index,
                        totalCount = shuffledWords.size,
                        correctCount = correctCount,
                        incorrectCount = incorrectCount,
                        keepKeyboardVisible = sessionPlan == PracticeSessionPlan.MIXED_CRAM_TEST,
                )
        }

        private fun buildTaskTypes(size: Int): List<TaskType> = when (sessionPlan) {
                PracticeSessionPlan.CRAM_ONLY -> List(size) { TaskType.CRAM }

                PracticeSessionPlan.MIXED_CRAM_TEST -> {
                        val testCount = (size * TEST_TASK_RATIO).roundToInt().coerceIn(0, size)
                        val cramCount = size - testCount
                        (List(cramCount) { TaskType.CRAM } + List(testCount) { TaskType.TEST }).shuffled()
                }

                PracticeSessionPlan.TEST_ONLY -> List(size) { TaskType.TEST }
        }

        private companion object {
                const val CRAM_PROGRESS_STEP = 0.15f
                const val CRAM_HINT_PROGRESS_STEP = 0.05f
                const val TEST_PROGRESS_STEP = 0.1f
                const val TEST_TASK_RATIO = 0.3f
        }
}
