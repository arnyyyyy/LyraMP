package com.arno.lyramp.feature.user_settings.domain.usecase

import com.arno.lyramp.feature.user_settings.data.UserSettingsRepository
import com.arno.lyramp.feature.user_settings.domain.model.LanguageSettings

class GetLanguageSettingsUseCase internal constructor(
        private val getSelectedLanguage: GetSelectedLanguageUseCase,
        private val repository: UserSettingsRepository
) {
        suspend operator fun invoke(): LanguageSettings {
                val lang = getSelectedLanguage() ?: "en"
                val wordLevel = repository.getWordLevelForLanguage(lang)
                return LanguageSettings(lang = lang, wordLevel = wordLevel)
        }
}
