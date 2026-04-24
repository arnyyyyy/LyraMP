package com.arno.lyramp.feature.extraction.domain.usecase

import com.arno.lyramp.feature.extraction.data.TrackStatusRepository

internal class GetExhaustedTrackIdsUseCase(
        private val repository: TrackStatusRepository,
) {
        suspend operator fun invoke(levelsKey: String) = repository.getExhaustedTrackIds(levelsKey)
}

internal class MarkTrackExhaustedUseCase(
        private val repository: TrackStatusRepository,
) {
        suspend operator fun invoke(trackId: String, trackName: String, levelsKey: String) =
                repository.markExhausted(trackId, trackName, levelsKey)
}
