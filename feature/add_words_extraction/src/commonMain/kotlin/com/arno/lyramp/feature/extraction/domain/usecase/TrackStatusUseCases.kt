package com.arno.lyramp.feature.extraction.domain.usecase

import com.arno.lyramp.feature.extraction.data.ExtractionTrackStatusDao
import com.arno.lyramp.feature.extraction.data.ExtractionTrackStatusEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class GetExhaustedTrackIdsUseCase(
        private val dao: ExtractionTrackStatusDao
) {
        suspend operator fun invoke(levelsKey: String): Set<String> = withContext(Dispatchers.IO) {
                dao.getExhaustedTrackIds(levelsKey).toSet()
        }
}

internal class MarkTrackExhaustedUseCase(
        private val dao: ExtractionTrackStatusDao
) {
        suspend operator fun invoke(trackId: String, trackName: String, levelsKey: String) = withContext(Dispatchers.IO) {
                val existing = dao.getStatus(trackId)
                val updated = (existing ?: ExtractionTrackStatusEntity(trackId = trackId, trackName = trackName))
                        .withExhaustedLevel(levelsKey)
                dao.upsert(updated)
        }
}
