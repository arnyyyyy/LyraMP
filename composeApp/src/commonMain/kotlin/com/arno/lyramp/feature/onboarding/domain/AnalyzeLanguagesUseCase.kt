package com.arno.lyramp.feature.onboarding.domain

import com.arno.lyramp.feature.listening_history.model.MusicTrack
import com.arno.lyramp.feature.translation.domain.TranslationState
import com.arno.lyramp.feature.translation.domain.TranslationRepository
import com.arno.lyramp.util.Log

internal class AnalyzeLanguagesUseCase(
        private val translationRepository: TranslationRepository
) {
        suspend operator fun invoke(tracks: List<MusicTrack>): Map<String, Int> {
                val languageCounts = mutableMapOf<String, Int>()

                tracks.take(35).forEach { track ->
                        try {
                                val result = translationRepository.translateWord(track.name)
                                if (result is TranslationState.Success) {
                                        result.translationWithLang.sourceLanguage?.let { lang ->
                                                languageCounts[lang] = (languageCounts[lang] ?: 0) + 1
                                        }
                                }
                        } catch (e: Exception) {
                                Log.logger.e(e) { "OnboardingScreenModel: failed to detect language" }
                        }
                }

                return languageCounts.toList()
                        .sortedByDescending { it.second }
                        .take(4)
                        .toMap()
        }
}