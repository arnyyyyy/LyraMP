package com.arno.lyramp.feature.extraction.data

internal class TrackStatusRepository(
        private val dao: ExtractionTrackStatusDao,
) {
        suspend fun getExhaustedTrackIds(levelsKey: String) = dao.getExhaustedTrackIds(levelsKey).toSet()

        suspend fun markExhausted(trackId: String, trackName: String, levelsKey: String) {
                val existing = dao.getStatus(trackId)
                val updated = (existing ?: ExtractionTrackStatusEntity(
                        trackId = trackId,
                        trackName = trackName,
                )).withExhaustedLevel(levelsKey)
                dao.upsert(updated)
        }
}
