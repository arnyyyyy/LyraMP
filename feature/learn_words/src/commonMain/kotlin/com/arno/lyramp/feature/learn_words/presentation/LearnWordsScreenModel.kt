package com.arno.lyramp.feature.learn_words.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.feature.extraction.domain.usecase.ClassifyWordsByCefrUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.translation.domain.GetSpeechFilePathUseCase
import com.arno.lyramp.feature.translation.speech.TranslationSpeechController
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

internal data class LearnWordsLaunchArgs(
        val mode: LearningMode,
        val language: String?,
        val cefrGroup: CefrDifficultyGroup?,
        val useMixedPractice: Boolean,
        val albumId: String? = null,
        val trackIndex: Int? = null,
)

internal class LearnWordsScreenModel(
        private val mode: LearningMode,
        private val language: String?,
        private val cefrGroup: CefrDifficultyGroup?,
        private val useMixedPractice: Boolean,
        private val albumId: String? = null,
        private val trackIndex: Int? = null,
        private val repository: LearnWordsRepository,
        private val getSpeechFilePath: GetSpeechFilePathUseCase,
        private val classifyWordsByCefr: ClassifyWordsByCefrUseCase,
) : ScreenModel {
        private val speechController = TranslationSpeechController()

        private val _uiState = MutableStateFlow<LearnWordsUiState>(LearnWordsUiState.Loading)
        val uiState: StateFlow<LearnWordsUiState> = _uiState.asStateFlow()

        private lateinit var cardsDelegate: CardsModeDelegate
        private lateinit var practiceDelegate: PracticeModeDelegate

        init {
                screenModelScope.launch {
                        val allWords = repository.getAllWords().first()
                        val baseFiltered = if (language != null) allWords.filter { it.sourceLang == language } else allWords

                        val filtered = if (albumId != null && trackIndex != null) {
                                baseFiltered.filter { it.albumId == albumId && it.trackIndex == trackIndex && !it.isKnown }
                        } else {
                                baseFiltered
                        }

                        val wordsToUse = if (cefrGroup != null) {
                                val grouped = withContext(Dispatchers.IO) {
                                        classifyWordsByCefr(filtered.map { it.word }, language ?: "en")
                                }
                                val wordsInGroup = grouped[cefrGroup]?.toSet() ?: emptySet()
                                filtered.filter { it.word in wordsInGroup }.ifEmpty { filtered }
                        } else {
                                filtered
                        }

                        if (wordsToUse.isEmpty()) return@launch

                        val shuffled = wordsToUse.shuffled()

                        when (mode) {
                                LearningMode.CARDS -> {
                                        cardsDelegate = CardsModeDelegate(
                                                shuffledWords = shuffled,
                                                repository = repository,
                                                getSpeechFilePath = getSpeechFilePath,
                                                speechController = speechController,
                                                uiState = _uiState,
                                                coroutineScope = screenModelScope,
                                        )
                                }

                                LearningMode.CRAM -> {
                                        practiceDelegate = PracticeModeDelegate(
                                                mode = mode,
                                                sessionPlan = if (useMixedPractice) {
                                                        PracticeSessionPlan.MIXED_CRAM_TEST
                                                } else {
                                                        PracticeSessionPlan.CRAM_ONLY
                                                },
                                                shuffledWords = shuffled,
                                                allFilteredWords = filtered,
                                                repository = repository,
                                                uiState = _uiState,
                                                coroutineScope = screenModelScope,
                                        )
                                }

                                LearningMode.TEST -> {
                                        practiceDelegate = PracticeModeDelegate(
                                                mode = mode,
                                                sessionPlan = PracticeSessionPlan.TEST_ONLY,
                                                shuffledWords = shuffled,
                                                allFilteredWords = filtered,
                                                repository = repository,
                                                uiState = _uiState,
                                                coroutineScope = screenModelScope,
                                        )
                                }
                        }
                }
        }

        fun onCardSwipe(wordId: Long, isKnown: Boolean) = cardsDelegate.onSwipe(wordId, isKnown)
        fun undoLastSwipe() = cardsDelegate.undoLastSwipe()
        fun onToggleImportance(wordId: Long, isImportant: Boolean) = cardsDelegate.onToggleImportance(wordId, isImportant)
        fun playAudio(word: WordInfo) = cardsDelegate.playAudio(word)

        fun onLearnInputChange(input: String) = practiceDelegate.onInputChange(input)
        fun onLearnHintShown() = practiceDelegate.onHintShown()
        fun onLearnCheck() = practiceDelegate.onCheck()
        fun onLearnSkip() = practiceDelegate.onSkip()
        fun onLearnNext() = practiceDelegate.onNext()

        fun onTestSelectOption(index: Int) = practiceDelegate.onSelectOption(index)
        fun onTestNext() = practiceDelegate.onNext()

        override fun onDispose() = speechController.stop()
}
