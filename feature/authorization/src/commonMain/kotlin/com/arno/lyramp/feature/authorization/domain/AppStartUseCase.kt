package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.data.AppleAuthRepository
import com.arno.lyramp.feature.authorization.data.AuthSelectionStorage
import com.arno.lyramp.feature.authorization.data.YandexAuthRepository

class AppStartUseCase internal constructor(
        private val yandexRepo: YandexAuthRepository,
        private val appleRepo: AppleAuthRepository
) {
        operator fun invoke(): AppStartDestination {
                val auth = AuthSelectionStorage.lastAuthorizedService

                return when {
                        auth == null -> AppStartDestination.Authorization
                        auth == "YANDEX" && !yandexRepo.getAccessToken().isNullOrEmpty() -> AppStartDestination.ShowListeningHistory
                        auth == "APPLE" && appleRepo.hasPlaylist() -> AppStartDestination.ShowListeningHistory
                        auth == "NONE" -> AppStartDestination.ShowListeningHistory
                        else -> AppStartDestination.Authorization
                }
        }
}
