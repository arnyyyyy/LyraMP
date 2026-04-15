package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository

internal class HideTrackUseCase(
        private val repository: ListeningHistoryRepository
) {
        suspend operator fun invoke(trackId: String) = repository.hideTrack(trackId)
}
