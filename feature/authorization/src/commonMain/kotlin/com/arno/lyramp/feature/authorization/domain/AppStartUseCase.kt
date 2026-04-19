package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.data.AppleAuthRepository
import com.arno.lyramp.feature.authorization.data.AuthSelectionStorage
import com.arno.lyramp.feature.authorization.data.YandexAuthRepository
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType

class AppStartUseCase internal constructor(
        private val yandexRepo: YandexAuthRepository,
        private val appleRepo: AppleAuthRepository
) {
        operator fun invoke(): AppStartDestination {
                val service = AuthSelectionStorage.lastAuthorizedService?.let { runCatching { MusicServiceType.valueOf(it) }.getOrNull() }

                return when (service) {
                        null -> AppStartDestination.Authorization
                        MusicServiceType.YANDEX ->
                                if (!yandexRepo.getAccessToken().isNullOrEmpty()) AppStartDestination.ShowListeningHistory
                                else AppStartDestination.Authorization

                        MusicServiceType.APPLE ->
                                if (appleRepo.hasPlaylist()) AppStartDestination.ShowListeningHistory
                                else AppStartDestination.Authorization

                        MusicServiceType.NONE -> AppStartDestination.ShowListeningHistory
                }
        }
}
