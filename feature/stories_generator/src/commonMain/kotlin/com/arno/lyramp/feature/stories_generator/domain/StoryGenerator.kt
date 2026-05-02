package com.arno.lyramp.feature.stories_generator.domain

import com.arno.lyramp.core.model.LyraLang
import com.arno.lyramp.feature.stories_generator.model.GeneratedStory
import com.arno.lyramp.feature.stories_generator.model.StoryGenre
import com.arno.lyramp.feature.stories_generator.model.StoryWord
import com.llamatik.library.platform.LlamaBridge
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

internal class StoryGenerator {

        private val nativeLock = Mutex()
        private var loadedModelPath: String? = null

        private fun applyGenerationParams() {
//                LlamaBridge.updateGenerateParams(
//                        temperature = 0.7f,
//                        maxTokens = 512
//                        topP = 0.9f,
//                        topK = 40,
//                        repeatPenalty = 1.1f
//                )
        }

        suspend fun loadModelFromPath(modelPath: String): Boolean = nativeLock.withLock {
                if (loadedModelPath == modelPath) return@withLock true
                withContext(Dispatchers.Default) {
                        try {
                                if (loadedModelPath != null) {
                                        try {
                                                LlamaBridge.shutdown()
                                        } catch (_: Throwable) {
                                        }
                                        loadedModelPath = null
                                }
                                val ok = LlamaBridge.initGenerateModel(modelPath)
                                if (ok) loadedModelPath = modelPath
                                ok
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (_: Exception) {
                                false
                        }
                }
        }

        @OptIn(ExperimentalTime::class)
        suspend fun generateStory(
                words: List<StoryWord>,
                language: String,
                genre: StoryGenre = StoryGenre.DRAMA
        ): GeneratedStory {
                val startTime = Clock.System.now().toEpochMilliseconds()

                applyGenerationParams()
                val raw = withContext(Dispatchers.Default) {
                        nativeLock.withLock {
                                LlamaBridge.generateWithContext(
                                        systemPrompt = systemPromptFor(genre, language),
                                        contextBlock = "",
                                        userPrompt = buildUserPrompt(words, language, genre)
                                )
                        }
                }
                val text = cleanResponse(raw)
                val title = deriveTitle(text, genre)
                val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
                return GeneratedStory(
                        title = title,
                        genre = genre,
                        text = text,
                        wordsUsed = words,
                        language = language,
                        generationTimeMs = elapsed,
                        createdAt = Clock.System.now().toEpochMilliseconds()
                )
        }

        suspend fun release() = nativeLock.withLock {
                if (loadedModelPath != null) {
                        LlamaBridge.shutdown()
                        loadedModelPath = null
                }
        }

        private fun cleanResponse(raw: String): String {
                var text = raw
                val imCut = text.indexOf("</start_of_turn>")
                if (imCut > 0) text = text.substring(0, imCut)
                return text.trim()
        }

        private fun deriveTitle(text: String, genre: StoryGenre): String {
                val firstSentence = text.substringBefore('.')
                        .substringBefore('\n')
                        .trim()
                if (firstSentence.isEmpty()) return genre.displayName
                val words = firstSentence.split(" ").take(6).joinToString(" ")
                return words.ifBlank { genre.displayName }
        }

        private fun systemPromptFor(genre: StoryGenre, language: String): String {
                val targetLanguage = LyraLang.llmPromptName(language)
                return "You are a storyteller." +
                    "Write ONLY in $targetLanguage. Given a list of words and a genre (${genre.promptHint}), " +
                    "write a short story (3-5 sentences) using all of them in the requested genre."
        }

        private fun buildUserPrompt(words: List<StoryWord>, language: String, genre: StoryGenre): String {
                val targetLanguage = LyraLang.llmPromptName(language)
                val selected = words
                        .map { it.word.lowercase() }
                        .filter { it.length > 2 }
                        .take(8)
                val wordList = selected.joinToString(", ")

                return "Language: $targetLanguage. Genre: ${genre.promptHint}. " +
                    "Write the story in $targetLanguage and use ALL OF words: $wordList"
        }
}
