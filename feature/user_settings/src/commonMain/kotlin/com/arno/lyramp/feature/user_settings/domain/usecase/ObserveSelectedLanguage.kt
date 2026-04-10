package com.arno.lyramp.feature.user_settings.domain.usecase

import com.arno.lyramp.feature.user_settings.data.UserSettingsRepository

class ObserveSelectedLanguageUseCase(private val repository: UserSettingsRepository) {
        operator fun invoke() = repository.selectedLanguageFlow
}
