package com.arno.lyramp.feature.stories_generator.domain

import com.arno.lyramp.feature.learn_words.data.LearnWordEntity
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoryRepository
import com.arno.lyramp.feature.stories_generator.model.GeneratedStory
import com.arno.lyramp.feature.stories_generator.model.StoryGenre
import com.arno.lyramp.feature.stories_generator.model.StoryWord
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class StoryGenerationService(
        private val getAllLearnWords: GetAllLearnWordsUseCase,
        private val modelDownloadRepository: ModelDownloadRepository,
        private val getSelectedLanguage: GetSelectedLanguageUseCase,
        private val repository: GeneratedStoryRepository
) {
        suspend fun generateAndSaveOne(
                wordsCount: Int = STORY_WORDS_COUNT,
                maxNoDuplicateSearchAttempts: Int = MAX_DEDUP_ATTEMPTS
        ) = generationMutex.withLock {
                val words = getAllLearnWords().first()
                if (words.size < MIN_WORDS) return@withLock null
                val modelPath = findModelPath() ?: run { return@withLock null }

                val language = getSelectedLanguage() ?: "en"
                val candidates = words.filter { it.sourceLang == language || it.sourceLang == null }
                if (candidates.size < MIN_WORDS) return@withLock null

                val generator = LlamatikStoryGenerator()
                try {
                        if (!generator.loadModelFromPath(modelPath)) {
                                return@withLock null
                        }
                        val (selected, genre) = pickUniqueCombo(
                                candidates = candidates,
                                language = language,
                                wordsCount = wordsCount,
                                maxAttempts = maxNoDuplicateSearchAttempts
                        ) ?: run { return@withLock null }

                        val story = generator.generateStory(selected, language, genre)
                        val newId = repository.save(story, isManual = false)
                        if (newId <= 0L) {
                                null
                        } else {
                                repository.trimToSize(MAX_CATALOG_SIZE)
                                story.copy(id = newId)
                        }
                } finally {
                        generator.release()
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

        private fun findModelPath(): String? {
                val downloadedModel = modelDownloadRepository.findDownloadedModel() ?: return null
                return modelDownloadRepository.getModelFilePath(downloadedModel)
        }

        companion object {
                const val MIN_UNREAD_TARGET = 4
                const val MAX_CATALOG_SIZE = 50
                const val STORY_WORDS_COUNT = 5
                const val MIN_WORDS = 5
                const val MAX_DEDUP_ATTEMPTS = 8

                private val generationMutex = Mutex()
        }
}

