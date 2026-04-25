package com.arno.lyramp.feature.listening_history.domain.service

internal class DynamicMusicService(private val factory: () -> MusicService) : MusicService {
        override suspend fun getListeningHistory(limit: Int?) = factory().getListeningHistory(limit)
}