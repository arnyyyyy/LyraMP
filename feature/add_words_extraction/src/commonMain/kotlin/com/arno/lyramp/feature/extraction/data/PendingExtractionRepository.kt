package com.arno.lyramp.feature.extraction.data

import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.feature.extraction.domain.model.ExtractedWord
import com.arno.lyramp.feature.extraction.domain.model.ExtractionResult

internal class PendingExtractionRepository(
        private val dao: ExtractionPendingWordsDao,
) {
        suspend fun save(result: ExtractionResult) {
                dao.deleteAll()
                if (result.words.isEmpty()) return
                dao.insertAll(result.words.map { it.toEntity() })
        }

        suspend fun consume(): ExtractionResult? {
                val words = dao.getAll().map { it.toDomain() }
                if (words.isEmpty()) return null

                dao.deleteAll()
                return ExtractionResult(
                        processedTracks = 0,
                        totalWords = 0,
                        newWords = words.size,
                        words = words,
                )
        }

        suspend fun clear() {
                dao.deleteAll()
        }

        private fun ExtractedWord.toEntity() = ExtractionPendingWordEntity(
                word = word,
                cefrLevel = cefrLevel?.name ?: UNKNOWN_LEVEL,
                lyricLine = lyricLine,
                trackName = trackName,
                artists = artists.joinToString(ARTIST_SEPARATOR),
                language = language,
        )

        private fun ExtractionPendingWordEntity.toDomain(): ExtractedWord {
                val level = CefrLevel.entries.firstOrNull { it.name == cefrLevel } // null = unclassified
                return ExtractedWord(
                        word = word,
                        cefrLevel = level,
                        lyricLine = lyricLine,
                        trackName = trackName,
                        artists = artists.split(ARTIST_SEPARATOR).filter { it.isNotBlank() },
                        language = language,
                )
        }

        private companion object {
                const val ARTIST_SEPARATOR = "\n"
                const val UNKNOWN_LEVEL = "UNKNOWN"
        }
}
