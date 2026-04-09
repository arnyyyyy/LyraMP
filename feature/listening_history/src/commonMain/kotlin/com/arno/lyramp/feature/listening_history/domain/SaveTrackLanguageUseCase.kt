package com.arno.lyramp.feature.listening_history.domain

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryDao

class SaveTrackLanguageUseCase(
        private val dao: ListeningHistoryDao
) {
        suspend operator fun invoke(trackId: String, language: String) {
                dao.updateLanguage(trackId, language)
        }
}
