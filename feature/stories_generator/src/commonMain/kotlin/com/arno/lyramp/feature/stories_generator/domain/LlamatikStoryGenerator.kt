package com.arno.lyramp.feature.stories_generator.domain

import com.arno.lyramp.feature.stories_generator.model.GeneratedStory
import com.arno.lyramp.feature.stories_generator.model.StoryWord
import com.arno.lyramp.util.Log
import com.llamatik.library.platform.LlamaBridge
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
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
                return withContext(Dispatchers.IO) {
                        try {
                                _isModelLoaded = LlamaBridge.initGenerateModel(modelPath)
                                _isModelLoaded
                        } catch (_: Exception) {
                                _isModelLoaded = false
                                false
                        }
                }
        }

        @OptIn(ExperimentalTime::class)
        suspend fun generateStory(
                words: List<StoryWord>,
                language: String
        ): GeneratedStory {
                val startTime = Clock.System.now().toEpochMilliseconds()

                applyGenerationParams()
                val raw = withContext(Dispatchers.IO) {
                        LlamaBridge.generateWithContext(
                                systemPrompt = SYSTEM_PROMPT,
                                contextBlock = "",
                                userPrompt = buildUserPrompt(words, language)
                        )
                }
                val text = cleanResponse(raw)
                val elapsed = Clock.System.now().toEpochMilliseconds() - startTime
                return GeneratedStory(
                        text = text,
                        wordsUsed = words,
                        generationTimeMs = elapsed
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

        private fun buildUserPrompt(words: List<StoryWord>, language: String): String {
                val selected = words
                        .map { it.word.lowercase() }
                        .filter { it.length > 2 }
                        .take(3)
                val wordList = selected.joinToString(", ")

                return "Write a story using ALL OF words:$wordList"
        }

        private companion object {
                const val SYSTEM_PROMPT = "You are a storyteller. Given a list of words, write a short story (3-5 sentences) using all of them."
                // TODO ЯЗЫК
        }
}
