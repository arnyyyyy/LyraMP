package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack

internal class HideTrackUseCase(
        private val repository: ListeningHistoryRepository
) {
        suspend operator fun invoke(track: ListeningHistoryMusicTrack) = repository.hideTrack(track)
}
