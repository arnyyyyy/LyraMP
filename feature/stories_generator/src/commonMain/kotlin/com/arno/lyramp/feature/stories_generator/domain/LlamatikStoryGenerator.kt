package com.arno.lyramp.feature.stories_generator.domain

import com.arno.lyramp.feature.stories_generator.model.GeneratedStory
import com.arno.lyramp.feature.stories_generator.model.StoryGenre
import com.arno.lyramp.feature.stories_generator.model.StoryWord
import com.arno.lyramp.util.Log
import com.llamatik.library.platform.LlamaBridge
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

class LlamatikStoryGenerator {

        private var _isModelLoaded = false

        private fun applyGenerationParams() {
//                LlamaBridge.updateGenerateParams(
//                        temperature = 0.7f,
//                        maxTokens = 512
//                        topP = 0.9f,
//                        topK = 40,
//                        repeatPenalty = 1.1f
//                )
        }

        suspend fun loadModelFromPath(modelPath: String): Boolean {
                return withContext(Dispatchers.Default) {
                        try {
                                _isModelLoaded = LlamaBridge.initGenerateModel(modelPath)
                                _isModelLoaded
                        } catch (ce: CancellationException) {
                                throw ce
                        } catch (_: Exception) {
                                _isModelLoaded = false
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
                        LlamaBridge.generateWithContext(
                                systemPrompt = systemPromptFor(genre),
                                contextBlock = "",
                                userPrompt = buildUserPrompt(words, language, genre)
                        )
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

        fun release() {
                if (_isModelLoaded) {
                        LlamaBridge.shutdown()
                        _isModelLoaded = false
                }
        }

        private fun cleanResponse(raw: String): String {
                var text = raw
                Log.logger.d { "AAAAA" + text }
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

        private fun systemPromptFor(genre: StoryGenre): String =
                "You are a storyteller. Given a list of words and a genre (${genre.promptHint}), " +
                    "write a short story (3-5 sentences) using all of them in the requested genre."

        private fun buildUserPrompt(words: List<StoryWord>, language: String, genre: StoryGenre): String {
                val selected = words
                        .map { it.word.lowercase() }
                        .filter { it.length > 2 }
                        .take(8)
                val wordList = selected.joinToString(", ")

                return "Genre: ${genre.promptHint}. Write a story using ALL OF words: $wordList"
                // TODO ЯЗЫК
        }
}
