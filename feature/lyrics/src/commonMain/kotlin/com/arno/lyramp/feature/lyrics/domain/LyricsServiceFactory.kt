package com.arno.lyramp.feature.lyrics.domain

import com.arno.lyramp.feature.authorization.domain.GetLastAuthorizedServiceUseCase
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType

internal class LyricsServiceFactory(
        private val yandexLyricsService: YandexLyricsService,
        private val lyricsOvhService: LyricsOvhService,
        private val geniusLyricsService: GeniusLyricsService,
        private val getLastAuthorizedService: GetLastAuthorizedServiceUseCase,
) {
        fun getPrimaryService(): LyricsService = when (currentService()) {
                MusicServiceType.YANDEX -> yandexLyricsService
                MusicServiceType.NONE, null -> geniusLyricsService
        }

        fun getFallbackService(): LyricsService = when (currentService()) {
                MusicServiceType.YANDEX -> geniusLyricsService
                MusicServiceType.NONE, null -> lyricsOvhService
        }

        private fun currentService(): MusicServiceType? = getLastAuthorizedService()?.let {
                runCatching { MusicServiceType.valueOf(it) }.getOrNull()
        }
}
