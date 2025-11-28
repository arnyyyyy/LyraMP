package com.arno.lyramp.feature.listening_history.domain

import com.arno.lyramp.feature.listening_history.model.MusicTrack

interface MusicService {
        suspend fun getListeningHistory(limit: Int = 20): List<MusicTrack>
        fun isAuthorized(): Boolean
}