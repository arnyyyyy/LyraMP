package com.arno.lyramp.feature.stats.data

import com.arno.lyramp.feature.stats.domain.model.TrackCefrWord

internal class StatsTrackCefrWordRepository(private val dao: StatsTrackCefrWordDao) {

        suspend fun getForLanguage(language: String) = dao.getForLanguage(language).map { it.toDomain() }

        suspend fun saveAll(words: List<TrackCefrWord>) = dao.insertAll(words.map { it.toEntity() })

        suspend fun deleteForTrack(trackId: String) = dao.deleteForTrack(trackId)

        private fun StatsTrackCefrWordEntity.toDomain() = TrackCefrWord(
                trackId = trackId,
                word = word,
                cefrLevel = cefrLevel,
                language = language,
        )

        private fun TrackCefrWord.toEntity() = StatsTrackCefrWordEntity(
                trackId = trackId,
                word = word,
                cefrLevel = cefrLevel,
                language = language,
        )
}
