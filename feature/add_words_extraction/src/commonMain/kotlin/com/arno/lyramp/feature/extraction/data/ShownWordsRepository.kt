package com.arno.lyramp.feature.extraction.data

import com.arno.lyramp.feature.extraction.domain.model.ExtractedWord

internal class ShownWordsRepository(
        private val dao: ExtractionShownWordsDao,
) {
        suspend fun markShown(words: List<ExtractedWord>) {
                if (words.isEmpty()) return
                dao.insertAll(
                        words.map { w ->
                                ExtractionShownWordsEntity(
                                        word = w.word.lowercase(),
                                        language = w.language,
                                )
                        }
                )
        }

        suspend fun markShownStrings(words: List<String>, language: String) {
                if (words.isEmpty()) return
                dao.insertAll(
                        words.map { word ->
                                ExtractionShownWordsEntity(
                                        word = word.lowercase(),
                                        language = language,
                                )
                        }
                )
        }

        suspend fun getForStatsLanguage(language: String) =
                dao.getWordsForLanguage(language).mapTo(mutableSetOf()) { it.lowercase() }

        suspend fun getForExtraction(trackLanguage: String) =
                dao.getWordsForExtraction(trackLanguage, LEGACY_GLOBAL_SHOWN_LANGUAGE)
                        .mapTo(mutableSetOf()) { it.lowercase() }
}
