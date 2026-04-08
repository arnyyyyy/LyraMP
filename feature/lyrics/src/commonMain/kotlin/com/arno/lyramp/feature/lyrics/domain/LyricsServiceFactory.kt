package com.arno.lyramp.feature.lyrics.domain

import com.arno.lyramp.feature.authorization.repository.AuthSelectionStorage
import com.arno.lyramp.util.Log

 class LyricsServiceFactory(
        private val yandexLyricsService: YandexLyricsService,
        private val lyricsOvhService: LyricsOvhService
) {
        fun getPrimaryService(): LyricsService {
                val authorizedService = AuthSelectionStorage.lastAuthorizedService
                Log.logger.d("AAAAALast authorized service: $authorizedService")

                return when (authorizedService) {
                        "YANDEX" -> yandexLyricsService
                        "SPOTIFY", "APPLE" -> lyricsOvhService
                        else -> lyricsOvhService
                }
        }

        fun getFallbackService(): LyricsService {
                return lyricsOvhService
        }
}
