package com.arno.lyramp.feature.extraction.domain

import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.core.model.LyraLang
import com.arno.lyramp.core.util.wordTokenSequence

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

        fun extractUnknownWords(
                lyrics: String,
                cefrVocab: Map<String, CefrLevel>,
                language: String = "en"
        ): List<Pair<String, String>> {
                if (language in LyraLang.CJK) return emptyList()
                val lines = lyrics.lineSequence().filter { it.isNotBlank() }.toList()
                val result = mutableListOf<Pair<String, String>>()
                val seen = mutableSetOf<String>()
                val minLength = if (language in setOf("he", "ar")) 2 else 3

                for (line in lines) {
                        for (word in line.lowercase().wordTokenSequence()) {
                                if (word.length < minLength || word in seen || word in cefrVocab) continue
                                seen.add(word)
                                result.add(word to line.trim())
                        }
                }
                return result
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
                val maxWordLen = cefrVocab.keys.maxOfOrNull { it.length }
                        ?.coerceAtMost(MAX_CJK_WORD_LEN) ?: return result

                for (line in lines) {
                        val lineLower = line.lowercase()
                        for (start in lineLower.indices) {
                                for (len in 2..minOf(maxWordLen, lineLower.length - start)) {
                                        val candidate = lineLower.substring(start, start + len)
                                        if (candidate !in result) {
                                                cefrVocab[candidate]?.let { level ->
                                                        result[candidate] = line.trim() to level
                                                }
                                        }
                                }
                        }
                }
                return result
        }

        private const val MAX_CJK_WORD_LEN = 6
}
