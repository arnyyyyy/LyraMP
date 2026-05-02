package com.arno.lyramp.feature.listening_history.domain.service

/* NB: декоратор
    Просто добавляет source_id трекам
 */
internal class SourceTaggedMusicService(
        private val sourceId: String,
        private val delegate: MusicService,
) : MusicService {
        override suspend fun getListeningHistory(limit: Int?) =
                delegate.getListeningHistory(limit).map { it.copy(sourceId = sourceId) }
}
