package com.arno.lyramp.feature.user_settings.domain.usecase

import com.arno.lyramp.feature.user_settings.data.UserSettingsRepository

class ObserveSelectedLanguageUseCase internal constructor(
        private val repository: UserSettingsRepository
) {
        operator fun invoke() = repository.selectedLanguageFlow
}
