package com.arno.lyramp.feature.lyrics.domain

import com.arno.lyramp.feature.authorization.repository.AuthSelectionStorage
import com.arno.lyramp.util.Log

internal class LyricsServiceFactory(
        private val yandexLyricsService: YandexLyricsService,
        private val lyricsOvhService: LyricsOvhService,
        private val geniusLyricsService: GeniusLyricsService
) {
        fun getPrimaryService(): LyricsService {
                val authorizedService = AuthSelectionStorage.lastAuthorizedService
                Log.logger.d("AAAAALast authorized service: $authorizedService")

                return when (authorizedService) {
                        "YANDEX" -> yandexLyricsService
                        "APPLE" -> geniusLyricsService
                        else -> geniusLyricsService
                }
        }

        fun getFallbackService(): LyricsService {
                val authorizedService = AuthSelectionStorage.lastAuthorizedService

                return when (authorizedService) {
                        "YANDEX" -> geniusLyricsService
                        else -> lyricsOvhService
                }
        }
}
