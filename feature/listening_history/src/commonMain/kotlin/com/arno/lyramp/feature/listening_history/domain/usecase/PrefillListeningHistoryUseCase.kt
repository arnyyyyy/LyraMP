package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack

class PrefillListeningHistoryUseCase internal constructor(
        private val repository: ListeningHistoryRepository
) {
        suspend operator fun invoke(
                tracks: List<ListeningHistoryMusicTrack>,
                trackLanguages: Map<String, String>,
        ) = repository.prefillFromOnboarding(tracks, trackLanguages)
}
