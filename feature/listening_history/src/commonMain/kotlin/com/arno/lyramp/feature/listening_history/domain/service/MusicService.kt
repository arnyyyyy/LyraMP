package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack

interface MusicService {
        suspend fun getListeningHistory(limit: Int = 20): List<ListeningHistoryMusicTrack>
}
