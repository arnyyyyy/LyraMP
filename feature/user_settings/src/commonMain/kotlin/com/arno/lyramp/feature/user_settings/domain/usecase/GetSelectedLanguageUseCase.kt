package com.arno.lyramp.feature.user_settings.domain.usecase

import com.arno.lyramp.feature.user_settings.data.UserSettingsRepository

class GetSelectedLanguageUseCase(private val repository: UserSettingsRepository) {
        operator fun invoke(): String? = repository.getSelectedLanguage()
}
