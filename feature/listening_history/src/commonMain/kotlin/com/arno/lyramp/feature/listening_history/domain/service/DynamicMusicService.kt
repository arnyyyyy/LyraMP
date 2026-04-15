package com.arno.lyramp.feature.listening_history.domain.service

internal class DynamicMusicService(initial: MusicService) : MusicService {
        private var delegate: MusicService = initial

        fun replaceDelegate(newDelegate: MusicService) {
                delegate = newDelegate
        }

        override suspend fun getListeningHistory(limit: Int) = delegate.getListeningHistory(limit)
}