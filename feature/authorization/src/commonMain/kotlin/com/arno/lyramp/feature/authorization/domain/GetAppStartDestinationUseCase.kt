package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.data.AuthSelectionStorage
import com.arno.lyramp.feature.authorization.data.YandexAuthRepository
import com.arno.lyramp.feature.authorization.domain.model.AppStartDestination
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType

class GetAppStartDestinationUseCase internal constructor(
        private val yandexRepo: YandexAuthRepository,
) {
        operator fun invoke(): AppStartDestination {
                val service = AuthSelectionStorage.lastAuthorizedService?.let { runCatching { MusicServiceType.valueOf(it) }.getOrNull() }

                return when (service) {
                        null -> AppStartDestination.Authorization

                        MusicServiceType.YANDEX ->
                                if (yandexRepo.provideValidAccessToken() != null) AppStartDestination.ShowListeningHistory
                                else AppStartDestination.Authorization

                        MusicServiceType.NONE -> AppStartDestination.ShowListeningHistory
                }
        }
}
