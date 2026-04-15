package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository

internal class AddManualTrackUseCase(
        private val repository: ListeningHistoryRepository,
) {
        suspend operator fun invoke(name: String, artist: String) = repository.addManualTrack(name, artist)
}
