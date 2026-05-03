package com.arno.lyramp.feature.user_settings.domain.usecase

import com.arno.lyramp.feature.user_settings.data.UserSettingsRepository

class GetLearningLanguagesUseCase internal constructor(
        private val repository: UserSettingsRepository
) {
        suspend operator fun invoke() = repository.getLearningLanguages()
}
