package com.arno.lyramp.feature.listening_history.domain.usecase

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryRepository

internal class GetListeningHistoryUseCase(
        private val repository: ListeningHistoryRepository
) {
        operator fun invoke(languageDetectionLimit: Int = 35) = repository.getListeningHistory(languageDetectionLimit)
}

