package com.arno.lyramp.feature.extraction.data

import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.core.model.WordDifficultyProvider
import com.arno.lyramp.feature.extraction.resources.Res
import com.arno.lyramp.util.Log

class CefrRepository : WordDifficultyProvider {
        private val cache = mutableMapOf<String, Map<String, CefrLevel>>()

        suspend fun <T> classifyWords(
                items: List<T>,
                language: String = "en",
                wordExtractor: (T) -> String
        ): Map<CefrDifficultyGroup, List<T>> {
                val vocabMap = getVocabularyMap(language)
                return items.mapNotNull { item ->
                        val level = vocabMap[wordExtractor(item).lowercase()]
                        if (level != null) CefrDifficultyGroup.entries.find { it.includesLevel(level) }?.let { it to item }
                        else null
                }.groupBy({ it.first }, { it.second })
        }

        override suspend fun getWordLevels(language: String): Map<String, CefrLevel> =
                getVocabularyMap(language) // TODO без бриджа

        suspend fun getVocabularyMap(language: String = "en"): Map<String, CefrLevel> {
                cache[language]?.let { return it }

                val map = loadLanguageCsv(language)
                cache[language] = map
                return map
        }

        private suspend fun loadLanguageCsv(language: String): Map<String, CefrLevel> {
                val fileName = "files/cefr_$language.csv"
                return try {
                        val text = Res.readBytes(fileName).decodeToString()
                        val map = mutableMapOf<String, CefrLevel>()

                        for (rawLine in text.lineSequence()) {
                                val line = rawLine.trim()
                                if (line.isBlank()) continue

                                val parts = line.split(",")
                                if (parts.size < 2) continue

                                val word = parts[0].trim().lowercase()
                                val level = CefrLevel.valueOf(parts.last().trim().uppercase())
                                map[word] = level
                        }
                        map
                } catch (e: Exception) {
                        Log.logger.e(e) { "CefrRepository: Failed to load CEFR data for language '$language': ${e.message}" }
                        emptyMap()
                }
        }
}
