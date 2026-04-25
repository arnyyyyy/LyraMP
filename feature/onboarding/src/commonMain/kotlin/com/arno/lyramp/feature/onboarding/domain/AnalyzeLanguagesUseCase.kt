package com.arno.lyramp.feature.onboarding.domain

import com.arno.lyramp.core.model.MusicTrack
import com.arno.lyramp.feature.translation.domain.TranslateWordWithStateUseCase
import com.arno.lyramp.feature.translation.domain.TranslationState
import com.arno.lyramp.util.Log
import kotlin.coroutines.cancellation.CancellationException

internal class AnalyzeLanguagesUseCase(
        private val translateWordWithState: TranslateWordWithStateUseCase
) {
        suspend operator fun invoke(tracks: List<MusicTrack>): AnalysisResult {
                val languageCounts = mutableMapOf<String, Int>()
                val trackLanguages = mutableMapOf<String, String>()

                var analysedTracksSize = 0

                tracks.asSequence().filter { !CYRILLIC_REGEX.containsMatchIn(it.name) }
                        .take(MAX_TRACKS)
                        .forEach { track ->
                                try {
                                        val result = translateWordWithState(track.name)
                                        if (result is TranslationState.Success) {
                                                result.translationWithLang.sourceLanguage?.let { lang ->
                                                        languageCounts[lang] = (languageCounts[lang] ?: 0) + 1
                                                        track.id?.let { id -> trackLanguages[id] = lang }
                                                        analysedTracksSize++
                                                }
                                        }
                                } catch (ce: CancellationException) {
                                        throw ce
                                } catch (e: Exception) {
                                        Log.logger.e(e) { "AnalyzeLanguagesUseCase: failed to detect language" }
                                }
                        }

                val languages = languageCounts.toList()
                        .sortedByDescending { it.second }
                        .take(TOP_LANGUAGES)
                        .toMap()

                return AnalysisResult(languages, trackLanguages, analysedTracksSize)
        }

        private companion object {
                const val MAX_TRACKS = 35
                const val TOP_LANGUAGES = 4
                val CYRILLIC_REGEX = Regex("[\u0400-\u04FF]")
        }
}
