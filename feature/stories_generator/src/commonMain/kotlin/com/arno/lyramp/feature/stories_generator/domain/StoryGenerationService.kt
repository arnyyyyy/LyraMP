package com.arno.lyramp.feature.stories_generator.domain

import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoryRepository
import com.arno.lyramp.feature.stories_generator.model.GeneratedStory
import com.arno.lyramp.feature.stories_generator.model.StoryGenre
import com.arno.lyramp.feature.stories_generator.model.StoryWord
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext

internal enum class StoryGenerationState {
        Idle,
        Background,
        Manual
}

internal class StoryGenerationService(
        private val getAllLearnWords: GetAllLearnWordsUseCase,
        private val modelDownloadRepository: ModelDownloadRepository,
        private val getSelectedLanguage: GetSelectedLanguageUseCase,
        private val repository: GeneratedStoryRepository,
        private val generator: StoryGenerator,
) {
        private val generationStateMutex = Mutex()
        private val generationMutex = Mutex()
        private var backgroundJob: Job? = null
        private var manualGenerationRequests = 0

        private val _generationState = MutableStateFlow(StoryGenerationState.Idle)
        val generationState: StateFlow<StoryGenerationState> = _generationState.asStateFlow()

        suspend fun generateBackgroundUntilTarget(
                language: String? = null,
                targetUnread: Int = MIN_UNREAD_TARGET,
                wordsCount: Int = STORY_WORDS_COUNT,
                maxNoDuplicateSearchAttempts: Int = MAX_DEDUP_ATTEMPTS,
        ): Int {
                val currentJob = startBackgroundGeneration() ?: return 0

                return try {
                        generationMutex.withLock {
                                val targetLanguage = language ?: getSelectedLanguage() ?: DEFAULT_LANGUAGE
                                val candidates = candidatesForLanguage(targetLanguage)
                                if (candidates.size < MIN_WORDS) return@withLock 0
                                val modelPath = findModelPath() ?: return@withLock 0
                                if (!generator.loadModelFromPath(modelPath)) return@withLock 0

                                var generated = 0
                                while (true) {
                                        currentCoroutineContext().ensureActive()
                                        if (repository.countUnreadByLanguage(targetLanguage) >= targetUnread) break

                                        generateAutoStoryLocked(
                                                language = targetLanguage,
                                                candidates = candidates,
                                                wordsCount = wordsCount,
                                                maxNoDuplicateSearchAttempts = maxNoDuplicateSearchAttempts,
                                        ) ?: break
                                        generated += 1
                                }
                                generated
                        }
                } finally {
                        finishBackgroundGeneration(currentJob)
                }
        }

        suspend fun cancelBackgroundGeneration() {
                val jobToCancel = generationStateMutex.withLock {
                        backgroundJob.also { job ->
                                if (job != null) {
                                        backgroundJob = null
                                        if (manualGenerationRequests == 0) {
                                                _generationState.value = StoryGenerationState.Idle
                                        }
                                }
                        }
                }
                jobToCancel?.cancelAndJoin()
        }

        suspend fun generateManualAndSave(
                words: List<StoryWord>,
                language: String,
                genre: StoryGenre
        ): GeneratedStory? {
                val jobToCancel = generationStateMutex.withLock {
                        manualGenerationRequests += 1
                        backgroundJob.also {
                                backgroundJob = null
                                _generationState.value = StoryGenerationState.Manual
                        }
                }

                try {
                        jobToCancel?.cancelAndJoin()
                        return generationMutex.withLock {
                                val modelPath = findModelPath() ?: return@withLock null
                                if (!generator.loadModelFromPath(modelPath)) return@withLock null

                                val story = generator.generateStory(words, language, genre)
                                val newId = repository.save(story, isManual = true)
                                if (newId > 0L) story.copy(id = newId) else story
                        }
                } finally {
                        generationStateMutex.withLock {
                                manualGenerationRequests = (manualGenerationRequests - 1).coerceAtLeast(0)
                                if (manualGenerationRequests == 0) {
                                        _generationState.value = if (backgroundJob?.isActive == true) {
                                                StoryGenerationState.Background
                                        } else {
                                                StoryGenerationState.Idle
                                        }
                                }
                        }
                }
        }

        private suspend fun generateAutoStoryLocked(
                language: String,
                candidates: List<LearnWordEntity>,
                wordsCount: Int,
                maxNoDuplicateSearchAttempts: Int
        ): GeneratedStory? {
                if (candidates.size < MIN_WORDS) return null

                val (selected, genre) = pickUniqueCombo(
                        candidates = candidates,
                        language = language,
                        wordsCount = wordsCount,
                        maxAttempts = maxNoDuplicateSearchAttempts
                ) ?: return null

                val story = generator.generateStory(selected, language, genre)
                val newId = repository.save(story, isManual = false)
                return if (newId <= 0L) {
                        null
                } else {
                        repository.trimToSizeByLanguage(language, MAX_CATALOG_SIZE)
                        story.copy(id = newId)
                }
        }

        private suspend fun pickUniqueCombo(
                candidates: List<LearnWordEntity>,
                language: String,
                wordsCount: Int,
                maxAttempts: Int
        ): Pair<List<StoryWord>, StoryGenre>? {
                repeat(maxAttempts) {
                        val genre = StoryGenre.random()
                        val selected = candidates.shuffled()
                                .take(wordsCount)
                                .map { StoryWord(word = it.word, translation = it.translation) }
                        if (!repository.wouldBeDuplicate(selected, genre, language)) {
                                return selected to genre
                        }
                }
                return null
        }

        private suspend fun candidatesForLanguage(language: String): List<LearnWordEntity> {
                val words = getAllLearnWords().first()
                return words.filter { it.sourceLang == language || it.sourceLang == null }
        }

        private suspend fun findModelPath(): String? = withContext(Dispatchers.IO) {
                modelDownloadRepository.findDownloadedModel()
                        ?.let { modelDownloadRepository.getModelFilePath(it) }
        }

        private suspend fun startBackgroundGeneration(): Job? {
                val currentJob = currentCoroutineContext()[Job] ?: return null
                val shouldStart = generationStateMutex.withLock {
                        if (manualGenerationRequests > 0 || backgroundJob?.isActive == true) {
                                false
                        } else {
                                backgroundJob = currentJob
                                _generationState.value = StoryGenerationState.Background
                                true
                        }
                }
                return currentJob.takeIf { shouldStart }
        }

        private suspend fun finishBackgroundGeneration(currentJob: Job) {
                generationStateMutex.withLock {
                        if (backgroundJob === currentJob) {
                                backgroundJob = null
                                if (manualGenerationRequests == 0) {
                                        _generationState.value = StoryGenerationState.Idle
                                }
                        }
                }
        }

        companion object {
                const val MIN_UNREAD_TARGET = 4
                const val MAX_CATALOG_SIZE = 50
                const val STORY_WORDS_COUNT = 5
                const val MIN_WORDS = 5
                const val MAX_DEDUP_ATTEMPTS = 8
                const val DEFAULT_LANGUAGE = "en"
        }
}
