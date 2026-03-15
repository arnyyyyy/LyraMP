package com.arno.lyramp.feature.listening_history.domain

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack

interface MusicService {
        suspend fun getListeningHistory(limit: Int = 20): List<ListeningHistoryMusicTrack>
}