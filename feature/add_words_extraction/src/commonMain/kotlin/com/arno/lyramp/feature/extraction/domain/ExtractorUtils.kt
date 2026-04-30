package com.arno.lyramp.feature.extraction.domain

import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.core.model.LyraLang
import com.arno.lyramp.core.util.wordTokenSequence
import kotlin.collections.iterator

object WordExtractionUtils {

        fun extractUniqueWords(
                lyrics: String,
                cefrVocab: Map<String, CefrLevel>,
                language: String = "en"
        ): Map<String, Pair<String, CefrLevel>> {
                val lines = lyrics.lineSequence().filter { it.isNotBlank() }.toList()

                return if (language in LyraLang.CJK) {
                        extractBySubstring(lines, cefrVocab)
                } else {
                        extractByRegex(lines, cefrVocab, language)
                }
        }

        private fun extractByRegex(
                lines: List<String>,
                cefrVocab: Map<String, CefrLevel>,
                language: String
        ): Map<String, Pair<String, CefrLevel>> {
                val result = mutableMapOf<String, Pair<String, CefrLevel>>()
                val minLength = if (language in setOf("he", "ar")) 2 else 3

                for (line in lines) {
                        for (word in line.lowercase().wordTokenSequence()) {
                                if (word.length < minLength || word in result) continue
                                cefrVocab[word]?.let { level -> result[word] = line.trim() to level }
                        }
                }
                return result
        }

        private fun extractBySubstring(
                lines: List<String>,
                cefrVocab: Map<String, CefrLevel>
        ): Map<String, Pair<String, CefrLevel>> {
                val result = mutableMapOf<String, Pair<String, CefrLevel>>()
                for (line in lines) {
                        for ((word, level) in cefrVocab) {
                                if (word.length >= 2 && word !in result && line.lowercase().contains(word)) {
                                        result[word] = line.trim() to level
                                }
                        }
                }
                return result
        }
}
