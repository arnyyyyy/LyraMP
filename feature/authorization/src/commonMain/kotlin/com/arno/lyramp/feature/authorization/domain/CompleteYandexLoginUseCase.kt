package com.arno.lyramp.feature.authorization.domain

import com.arno.lyramp.feature.authorization.data.YandexAuthRepository

class CompleteYandexLoginUseCase internal constructor(
        private val yandexRepo: YandexAuthRepository,
) {
        operator fun invoke(token: String, expiresIn: Long?) = yandexRepo.saveAccessToken(token, expiresIn)
}
