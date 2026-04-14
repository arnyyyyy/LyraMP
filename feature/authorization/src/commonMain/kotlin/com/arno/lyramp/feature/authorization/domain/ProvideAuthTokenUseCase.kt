package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.repository.YandexAuthRepository

class ProvideAuthTokenUseCase internal constructor(
        private val yandexRepo: YandexAuthRepository,
) {
        suspend fun provide(service: MusicServiceType) = when (service) {
                MusicServiceType.YANDEX -> yandexRepo.provideValidAccessToken()
                else -> null
        }
}
