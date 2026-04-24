package com.arno.lyramp.feature.stats.data

import com.arno.lyramp.feature.stats.domain.model.TrackStatsMeta

internal class StatsTrackMetaRepository(private val dao: StatsTrackMetaDao) {
        suspend fun getForLanguage(language: String) =
                dao.getForLanguage(language).map { it.toDomain() }

        suspend fun getAllProcessedTrackIds() = dao.getAllProcessedTrackIds()

        suspend fun save(meta: TrackStatsMeta) = dao.upsert(meta.toEntity())

        private fun StatsTrackMetaEntity.toDomain() = TrackStatsMeta(
                trackId = trackId,
                trackName = trackName,
                artists = artists,
                language = language,
                totalWordsInLyrics = totalWordsInLyrics,
                uniqueCefrWordsCount = uniqueCefrWordsCount,
                processedAt = processedAt,
        )

        private fun TrackStatsMeta.toEntity() = StatsTrackMetaEntity(
                trackId = trackId,
                trackName = trackName,
                artists = artists,
                language = language,
                totalWordsInLyrics = totalWordsInLyrics,
                uniqueCefrWordsCount = uniqueCefrWordsCount,
                processedAt = processedAt,
        )
}
