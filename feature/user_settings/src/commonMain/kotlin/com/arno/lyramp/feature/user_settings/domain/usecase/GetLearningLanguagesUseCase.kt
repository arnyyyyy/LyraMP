package com.arno.lyramp.feature.user_settings.domain.usecase

import com.arno.lyramp.feature.user_settings.data.UserSettingsRepository

class GetLearningLanguagesUseCase(private val repository: UserSettingsRepository) {
        operator fun invoke(): Set<String> = repository.getLearningLanguages()
}
