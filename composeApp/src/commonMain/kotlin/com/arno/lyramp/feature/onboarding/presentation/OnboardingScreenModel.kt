package com.arno.lyramp.feature.onboarding.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.feature.listening_history.domain.MusicService
import com.arno.lyramp.feature.listening_history.model.MusicTrack
import com.arno.lyramp.feature.onboarding.model.OnboardingStep
import com.arno.lyramp.feature.translation.presentation.TranslationState
import com.arno.lyramp.feature.translation.repository.TranslationRepository
import com.arno.lyramp.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlin.collections.forEach

internal class OnboardingScreenModel(
        private val musicService: MusicService,
        private val translationRepository: TranslationRepository
) : ScreenModel {

        private val _state = MutableStateFlow<OnboardingState>(OnboardingState.Loading(OnboardingStep.LOADING_HISTORY))
        val state: StateFlow<OnboardingState> = _state.asStateFlow()

        init {
                startOnboarding()
        }

        private fun startOnboarding() {
                screenModelScope.launch {
                        try {
                                _state.value = OnboardingState.Loading(OnboardingStep.LOADING_HISTORY)
                                val tracks = musicService.getListeningHistory(limit = 50)

                                if (tracks.isEmpty()) {
                                        _state.value = OnboardingState.Error("Не удалось загрузить треки")
                                        return@launch
                                }

                                _state.value = OnboardingState.Loading(OnboardingStep.ANALYZING_LANGUAGES)
                                val languages = analyzeLanguages(tracks)

                                _state.value = OnboardingState.Success(
                                        step = OnboardingStep.SELECT_LANGUAGES,
                                        tracks = tracks,
                                        languages = languages
                                )

                        } catch (e: Exception) {
                                Log.logger.e(e) { "OnboardingScreenModel: error during onboarding" }
                                _state.value = OnboardingState.Error(e.message ?: "Неизвестная ошибка")
                        }
                }
        }

        private suspend fun analyzeLanguages(tracks: List<MusicTrack>): Map<String, Int> {
                val languageCounts = mutableMapOf<String, Int>()

                tracks.take(100).forEach { track ->
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

        fun retry() {
                startOnboarding()
        }
}

internal sealed interface OnboardingState {
        data class Loading(val step: OnboardingStep) : OnboardingState
        data class Success(
                val step: OnboardingStep,
                val tracks: List<MusicTrack>,
                val languages: Map<String, Int>
        ) : OnboardingState

        data class Error(val message: String) : OnboardingState
}
