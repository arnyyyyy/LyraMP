package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository

internal class UpdateTrackLanguageUseCase(
        private val repository: ListeningHistoryRepository
) {
        suspend operator fun invoke(trackId: String, language: String) {
                repository.updateTrackLanguage(trackId, language)
        }
}
