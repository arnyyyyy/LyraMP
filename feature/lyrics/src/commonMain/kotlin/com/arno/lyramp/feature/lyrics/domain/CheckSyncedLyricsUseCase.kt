package com.arno.lyramp.feature.lyrics.domain

class CheckSyncedLyricsUseCase internal constructor(
        private val yandexLyricsService: YandexLyricsService,
) {
        suspend operator fun invoke(artist: String, song: String, trackId: String?): Boolean {
                if (trackId.isNullOrBlank()) return false
                val lrc = yandexLyricsService.getTimestampedLyrics(artist, song, trackId)
                return !lrc.isNullOrBlank()
        }
}
