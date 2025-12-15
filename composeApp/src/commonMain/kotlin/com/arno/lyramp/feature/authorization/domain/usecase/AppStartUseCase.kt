package com.arno.lyramp.feature.authorization.domain.usecase

import com.arno.lyramp.feature.authorization.repository.AuthApiRepository
import com.arno.lyramp.feature.authorization.repository.AuthPlaylistRepository
import com.arno.lyramp.feature.authorization.repository.AuthSelectionStorage

class AppStartUseCase(
        private val spotifyRepo: AuthApiRepository,
        private val yandexRepo: AuthPlaylistRepository
) {
        operator fun invoke(): AppStartDestination {
                val last = AuthSelectionStorage.lastAuthorizedService

                return when {
                        last == null -> AppStartDestination.Authorization
                        last == "SPOTIFY" && !spotifyRepo.getAccessToken().isNullOrEmpty() -> AppStartDestination.ShowListeningHistory
                        last == "YANDEX" && yandexRepo.hasPlaylist() -> AppStartDestination.ShowListeningHistory
                        else -> AppStartDestination.Authorization
                }
        }
}

