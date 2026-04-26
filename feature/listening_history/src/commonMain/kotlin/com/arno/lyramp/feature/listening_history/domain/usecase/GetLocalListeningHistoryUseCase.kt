package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository

internal class GetLocalListeningHistoryUseCase(
        private val repository: ListeningHistoryRepository,
) {
        suspend operator fun invoke() = repository.getVisibleTracks()
}
