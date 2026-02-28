package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.repository.AppleAuthRepository
import com.arno.lyramp.feature.authorization.repository.AuthSelectionStorage
import com.arno.lyramp.feature.authorization.repository.SpotifyAuthRepository
import com.arno.lyramp.feature.authorization.repository.YandexAuthRepository

internal class AppStartUseCase(
        private val spotifyRepo: SpotifyAuthRepository,
        private val yandexRepo: YandexAuthRepository,
        private val appleRepo: AppleAuthRepository
) {
        operator fun invoke(): AppStartDestination {
                val auth = AuthSelectionStorage.lastAuthorizedService

                return when {
                        auth == null -> AppStartDestination.Authorization
                        auth == "SPOTIFY" && !spotifyRepo.getAccessToken().isNullOrEmpty() -> AppStartDestination.ShowListeningHistory
                        auth == "YANDEX" && !yandexRepo.getAccessToken().isNullOrEmpty() -> AppStartDestination.ShowListeningHistory
                        auth == "APPLE" && appleRepo.hasPlaylist() -> AppStartDestination.ShowListeningHistory
                        else -> AppStartDestination.Authorization
                }
        }
}
