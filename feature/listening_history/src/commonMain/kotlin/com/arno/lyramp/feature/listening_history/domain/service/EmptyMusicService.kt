package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack

internal class EmptyMusicService : MusicService {
        override suspend fun getListeningHistory(limit: Int?): List<ListeningHistoryMusicTrack> = emptyList()
}
