package com.arno.lyramp.feature.listening_history.domain.service

internal class SourceTaggedMusicService(
        private val sourceId: String,
        private val delegate: MusicService,
) : MusicService {
        override suspend fun getListeningHistory(limit: Int?) = delegate.getListeningHistory(limit).map { it.copy(sourceId = sourceId) }
}
