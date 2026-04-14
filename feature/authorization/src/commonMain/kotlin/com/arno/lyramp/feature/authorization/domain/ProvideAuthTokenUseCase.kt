package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.data.YandexAuthRepository

class ProvideAuthTokenUseCase internal constructor(
        private val yandexRepo: YandexAuthRepository,
) {
        operator fun invoke(service: MusicServiceType) = when (service) {
                MusicServiceType.YANDEX -> yandexRepo.provideValidAccessToken()
                else -> null
        }
}
