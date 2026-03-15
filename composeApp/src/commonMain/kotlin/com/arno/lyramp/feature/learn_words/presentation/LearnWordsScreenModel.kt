package com.arno.lyramp.feature.learn_words.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.learn_words.data.LanguagePreferencesRepository
import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.translation.domain.TranslationRepository
import com.arno.lyramp.feature.translation.model.WordInfo
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

internal class LearnWordsScreenModel(
        private val repository: LearnWordsRepository,
        private val languagePreferencesRepository: LanguagePreferencesRepository,
        private val translationRepository: TranslationRepository
) : ScreenModel {

        private data class InternalState(
                val allWords: List<LearnWordEntity> = emptyList(),
                val shuffledWords: List<LearnWordEntity> = emptyList(),
                val correctCount: Int = 0,
                val incorrectCount: Int = 0,
        )

        private val _internalState = MutableStateFlow(InternalState())

        private val _uiState = MutableStateFlow<LearnWordsUiState>(LearnWordsUiState.Loading)
        val uiState: StateFlow<LearnWordsUiState> = _uiState.asStateFlow()

        private val _selectedLanguage = MutableStateFlow<String?>(null)
        val selectedLanguage: StateFlow<String?> = _selectedLanguage.asStateFlow()

        private val _availableLanguages = MutableStateFlow<List<String>>(emptyList())
        val availableLanguages: StateFlow<List<String>> = _availableLanguages.asStateFlow()

        private val savedLanguage = languagePreferencesRepository.getSavedLanguage()

        init {
                screenModelScope.launch {
                        repository.getAllWords().collect { words ->
                                _internalState.update { it.copy(allWords = words) }

                                val languages = words.mapNotNull { it.sourceLang }.distinct().sorted()
                                _availableLanguages.value = languages

                                if (_selectedLanguage.value == null) {
                                        _selectedLanguage.value =
                                                savedLanguage?.takeIf { it in languages }
                                                        ?: languages.firstOrNull()
                                }

                                updateModeSelectionIfNeeded()
                        }
                }
        }

        private fun updateModeSelectionIfNeeded() {
                val current = _uiState.value
                if (current is LearnWordsUiState.Loading ||
                        current is LearnWordsUiState.ModeSelection ||
                        current is LearnWordsUiState.Empty
                ) {
                        val filtered = getFilteredWords()
                        _uiState.value = if (filtered.isEmpty()) {
                                LearnWordsUiState.Empty
                        } else {
                                LearnWordsUiState.ModeSelection(filtered.map { it.toDomain() })
                        }
                }
        }

        private fun getFilteredWords(): List<LearnWordEntity> {
                val allWords = _internalState.value.allWords
                val lang = _selectedLanguage.value ?: return allWords
                return allWords.filter { it.sourceLang == lang }
        }

        fun selectLanguage(language: String) {
                _selectedLanguage.value = language
                languagePreferencesRepository.saveLanguage(language)

                val filtered = getFilteredWords()
                _uiState.value = if (filtered.isEmpty()) {
                        LearnWordsUiState.Empty
                } else {
                        LearnWordsUiState.ModeSelection(filtered.map { it.toDomain() })
                }
        }

        fun onSelectMode(mode: LearningMode) {
                val filteredWords = getFilteredWords()
                if (filteredWords.isEmpty()) return
                val shuffled = filteredWords.shuffled()
                _internalState.update { it.copy(shuffledWords = shuffled, correctCount = 0, incorrectCount = 0) }

                when (mode) {
                        LearningMode.CARDS -> {
                                _uiState.value = LearnWordsUiState.Cards(
                                        words = shuffled.map { it.toDomain() },
                                        currentIndex = 0,
                                        correctCount = 0,
                                        incorrectCount = 0,
                                        totalCount = shuffled.size
                                )
                        }

                        LearningMode.CRAM -> startLearn(0)
                        LearningMode.TEST -> startTest(0)
                }
        }

        fun onCardSwipe(wordId: Long, isKnown: Boolean) {
                val state = _uiState.value
                if (state is LearnWordsUiState.Cards) {
                        val internal = _internalState.value
                        val newCorrect = if (isKnown) internal.correctCount + 1 else internal.correctCount
                        val newIncorrect = if (!isKnown) internal.incorrectCount + 1 else internal.incorrectCount
                        _internalState.update { it.copy(correctCount = newCorrect, incorrectCount = newIncorrect) }

                        val next = state.currentIndex + 1
                        if (next >= state.words.size) {
                                _uiState.value = LearnWordsUiState.Completed(
                                        mode = LearningMode.CARDS,
                                        correctCount = newCorrect,
                                        incorrectCount = newIncorrect,
                                        totalCount = state.totalCount
                                )
                        } else {
                                _uiState.value = state.copy(
                                        currentIndex = next,
                                        correctCount = newCorrect,
                                        incorrectCount = newIncorrect
                                )
                        }
                }

                screenModelScope.launch {
                        repository.markAsKnown(wordId, isKnown)
                }
        }

        fun onToggleImportance(wordId: Long, isImportant: Boolean) {
                screenModelScope.launch {
                        repository.toggleImportance(wordId, !isImportant)
                }
                val state = _uiState.value
                if (state is LearnWordsUiState.Cards) {
                        val updatedWords = state.words.map {
                                if (it.id == wordId) it.copy(isImportant = !isImportant) else it
                        }
                        _uiState.value = state.copy(words = updatedWords)
                }
        }

        fun onLearnInputChange(input: String) {
                val state = _uiState.value
                if (state is LearnWordsUiState.Cram) {
                        _uiState.value = state.copy(userInput = input, result = null)
                }
        }

        fun onLearnCheck() {
                val state = _uiState.value
                if (state is LearnWordsUiState.Cram && state.result == null) {
                        val correct = state.word.word.trim().equals(state.userInput.trim(), ignoreCase = true)
                        val internal = _internalState.value
                        val newCorrect = if (correct) internal.correctCount + 1 else internal.correctCount
                        val newIncorrect = if (!correct) internal.incorrectCount + 1 else internal.incorrectCount
                        _internalState.update { it.copy(correctCount = newCorrect, incorrectCount = newIncorrect) }
                        _uiState.value = state.copy(
                                result = if (correct) CheckResult.CORRECT else CheckResult.INCORRECT,
                                correctCount = newCorrect,
                                incorrectCount = newIncorrect
                        )
                }
        }

        fun onLearnNext() {
                val state = _uiState.value
                if (state is LearnWordsUiState.Cram) {
                        val next = state.currentIndex + 1
                        val shuffledWords = _internalState.value.shuffledWords
                        val internal = _internalState.value
                        if (next >= shuffledWords.size) {
                                _uiState.value = LearnWordsUiState.Completed(
                                        mode = LearningMode.CRAM,
                                        correctCount = internal.correctCount,
                                        incorrectCount = internal.incorrectCount,
                                        totalCount = shuffledWords.size
                                )
                        } else {
                                startLearn(next)
                        }
                }
        }

        private fun startLearn(index: Int) {
                val internal = _internalState.value
                val word = internal.shuffledWords[index]
                _uiState.value = LearnWordsUiState.Cram(
                        word = word.toDomain(),
                        userInput = "",
                        result = null,
                        currentIndex = index,
                        totalCount = internal.shuffledWords.size,
                        correctCount = internal.correctCount,
                        incorrectCount = internal.incorrectCount
                )
        }

        fun onTestSelectOption(index: Int) {
                val state = _uiState.value
                if (state is LearnWordsUiState.Test && !state.isAnswered) {
                        val correct = state.options[index] == state.correctAnswer
                        val internal = _internalState.value
                        val newCorrect = if (correct) internal.correctCount + 1 else internal.correctCount
                        val newIncorrect = if (!correct) internal.incorrectCount + 1 else internal.incorrectCount
                        _internalState.update { it.copy(correctCount = newCorrect, incorrectCount = newIncorrect) }
                        _uiState.value = state.copy(
                                selectedIndex = index,
                                correctCount = newCorrect,
                                incorrectCount = newIncorrect
                        )
                }
        }

        fun onTestNext() {
                val state = _uiState.value
                if (state is LearnWordsUiState.Test) {
                        val next = state.currentIndex + 1
                        val internal = _internalState.value
                        if (next >= internal.shuffledWords.size) {
                                _uiState.value = LearnWordsUiState.Completed(
                                        mode = LearningMode.TEST,
                                        correctCount = internal.correctCount,
                                        incorrectCount = internal.incorrectCount,
                                        totalCount = internal.shuffledWords.size
                                )
                        } else {
                                startTest(next)
                        }
                }
        }

        private fun startTest(index: Int) {
                val internal = _internalState.value
                val word = internal.shuffledWords[index]
                val variant = TestVariant.entries.random()
                val others = getFilteredWords().filter { it.id != word.id }.shuffled()

                val (correctAnswer, wrongAnswers) = when (variant) {
                        TestVariant.FOREIGN_TO_TRANSLATION -> {
                                word.translation to others.take(3).map { it.translation }
                        }

                        TestVariant.TRANSLATION_TO_FOREIGN -> {
                                word.word to others.take(3).map { it.word }
                        }
                }

                val options = (wrongAnswers + correctAnswer).shuffled()

                _uiState.value = LearnWordsUiState.Test(
                        word = word.toDomain(),
                        options = options,
                        correctAnswer = correctAnswer,
                        selectedIndex = null,
                        variant = variant,
                        currentIndex = index,
                        totalCount = internal.shuffledWords.size,
                        correctCount = internal.correctCount,
                        incorrectCount = internal.incorrectCount
                )
        }

        fun onRestart() {
                onBackToModes()
        }

        fun onBackToModes() {
                val filtered = getFilteredWords()
                _uiState.value = if (filtered.isEmpty()) {
                        LearnWordsUiState.Empty
                } else {
                        LearnWordsUiState.ModeSelection(filtered.map { it.toDomain() })
                }
        }

        suspend fun getSourceSpeechFilePath(wordInfo: WordInfo): String? {
                return withContext(Dispatchers.IO) {
                        translationRepository.getSourceSpeechFilePath(wordInfo)
                }
        }
}
