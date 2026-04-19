package com.arno.lyramp.feature.album_suggestion.domain.model

import com.arno.lyramp.core.model.CefrLevel
import com.arno.lyramp.feature.album_suggestion.data.ExtractionCandidateEntity
import com.arno.lyramp.feature.extraction.data.CefrRepository
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

internal class CandidateMapper(
        private val cefrRepository: CefrRepository
) {
        private val cacheMutex = Mutex()
        private val cefrVocabCache = mutableMapOf<String, Map<String, CefrLevel>>()

        suspend fun toSuggestedWord(entity: ExtractionCandidateEntity): SuggestedWord {
                val lang = entity.sourceLang ?: "en"
                val level = cefrLookup(lang, entity.word)
                return SuggestedWord(
                        word = entity.word,
                        cefrLevel = level,
                        lyricLine = entity.lyricLine,
                        trackName = entity.trackName,
                        artists = entity.artists,
                        trackIndex = entity.trackIndex
                )
        }

        suspend fun toSuggestedWords(entities: List<ExtractionCandidateEntity>): List<SuggestedWord> =
                entities.map { toSuggestedWord(it) }

        fun toEntity(
                word: SuggestedWord,
                albumId: String,
                lang: String
        ) = ExtractionCandidateEntity(
                word = word.word,
                sourceLang = lang,
                lyricLine = word.lyricLine,
                trackName = word.trackName,
                artists = word.artists,
                albumId = albumId,
                trackIndex = word.trackIndex,
                cefrLevel = word.cefrLevel?.name
        )

        fun toEntities(
                words: List<SuggestedWord>,
                albumId: String,
                lang: String
        ): List<ExtractionCandidateEntity> = words.map { toEntity(it, albumId, lang) }

        private suspend fun cefrLookup(lang: String, word: String): CefrLevel? {
                val map = cacheMutex.withLock { cefrVocabCache[lang] } ?: loadVocab(lang)
                return map[word.lowercase()]
        }

        private suspend fun loadVocab(lang: String): Map<String, CefrLevel> {
                return cacheMutex.withLock {
                        cefrVocabCache[lang]?.let { return@withLock it }
                        val loaded = runCatching { cefrRepository.getVocabularyMap(lang) }.getOrNull()
                        if (loaded != null) cefrVocabCache[lang] = loaded
                        loaded ?: emptyMap()
                }
        }
}
