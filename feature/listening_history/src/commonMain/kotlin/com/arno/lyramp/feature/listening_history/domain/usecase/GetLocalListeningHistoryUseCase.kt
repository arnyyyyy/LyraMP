package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository

class GetLocalListeningHistoryUseCase internal constructor(
        private val repository: ListeningHistoryRepository,
) {
        suspend operator fun invoke() = repository.getVisibleTracks()
}
