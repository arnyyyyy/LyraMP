package com.arno.lyramp.feature.authorization.domain.usecase

import com.arno.lyramp.feature.authorization.repository.SpotifyAuthRepository
import com.arno.lyramp.feature.authorization.repository.YandexAuthRepository
import com.arno.lyramp.feature.authorization.repository.AppleAuthRepository
import com.arno.lyramp.feature.authorization.repository.AuthSelectionStorage

internal class AppStartUseCase(
        private val spotifyRepo: SpotifyAuthRepository,
        private val yandexRepo: YandexAuthRepository,
        private val appleRepo: AppleAuthRepository
) {
        operator fun invoke(): AppStartDestination {
                val last = AuthSelectionStorage.lastAuthorizedService

                return when {
                        last == null -> AppStartDestination.Authorization
                        last == "SPOTIFY" && !spotifyRepo.getAccessToken().isNullOrEmpty() -> AppStartDestination.ShowListeningHistory
                        last == "YANDEX" && !yandexRepo.getAccessToken().isNullOrEmpty() -> AppStartDestination.ShowListeningHistory
                        last == "APPLE" && appleRepo.hasPlaylist() -> AppStartDestination.ShowListeningHistory
                        else -> AppStartDestination.Authorization
                }
        }
}
