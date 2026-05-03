package com.arno.lyramp.feature.user_settings.domain.usecase

import com.arno.lyramp.feature.user_settings.data.UserSettingsRepository

class SaveSelectedLanguageUseCase internal constructor(
        private val repository: UserSettingsRepository
) {
        operator fun invoke(language: String?) = repository.saveSelectedLanguage(language)
}
