package com.arno.lyramp.feature.onboarding.domain

import com.arno.lyramp.core.model.MusicTrack
import com.arno.lyramp.feature.translation.domain.TranslationState
import com.arno.lyramp.feature.translation.domain.TranslationRepository
import com.arno.lyramp.util.Log

internal data class AnalysisResult(
        val languages: Map<String, Int>,
        val trackLanguages: Map<String, String>
)

private val cyrillicRegex = Regex("[\u0400-\u04FF]")

internal class AnalyzeLanguagesUseCase(
        private val translationRepository: TranslationRepository
) {
        suspend operator fun invoke(tracks: List<MusicTrack>): AnalysisResult {
                val languageCounts = mutableMapOf<String, Int>()
                val trackLanguages = mutableMapOf<String, String>()

                tracks
                        .filter { !cyrillicRegex.containsMatchIn(it.name) }
                        .take(35)
                        .forEach { track ->
                                try {
                                        val result = translationRepository.translateWord(track.name)
                                        if (result is TranslationState.Success) {
                                                result.translationWithLang.sourceLanguage?.let { lang ->
                                                        languageCounts[lang] = (languageCounts[lang] ?: 0) + 1
                                                        track.id?.let { id -> trackLanguages[id] = lang }
                                                }
                                        }
                                } catch (e: Exception) {
                                        Log.logger.e(e) { "AnalyzeLanguagesUseCase: failed to detect language" }
                                }
                        }

                val languages = languageCounts.toList()
                        .sortedByDescending { it.second }
                        .take(4)
                        .toMap()

                return AnalysisResult(languages = languages, trackLanguages = trackLanguages)
        }
}
