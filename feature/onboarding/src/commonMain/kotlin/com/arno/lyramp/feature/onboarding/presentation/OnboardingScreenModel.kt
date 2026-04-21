package com.arno.lyramp.feature.onboarding.presentation

import cafe.adriel.voyager.core.model.ScreenModel
import cafe.adriel.voyager.core.model.screenModelScope
import com.arno.lyramp.core.model.MusicTrack
import com.arno.lyramp.feature.listening_history.domain.service.MusicService
import com.arno.lyramp.feature.listening_history.domain.usecase.PrefillListeningHistoryUseCase
import com.arno.lyramp.feature.onboarding.domain.AnalyzeLanguagesUseCase
import com.arno.lyramp.feature.onboarding.model.OnboardingStep
import com.arno.lyramp.feature.onboarding.presentation.OnboardingState.Error
import com.arno.lyramp.feature.onboarding.presentation.OnboardingState.Loading
import com.arno.lyramp.feature.onboarding.presentation.OnboardingState.Success
import com.arno.lyramp.util.Log
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

internal class OnboardingScreenModel(
        private val musicService: MusicService,
        private val analyzeLanguages: AnalyzeLanguagesUseCase,
        private val prefillListeningHistory: PrefillListeningHistoryUseCase
) : ScreenModel {
        private val _state = MutableStateFlow<OnboardingState>(Loading(OnboardingStep.LOADING_HISTORY))
        val state: StateFlow<OnboardingState> = _state.asStateFlow()

        init {
                load()
        }

        internal fun retry() {
                if (_state.value is Loading) return
                load()
        }

        private fun load() {
                screenModelScope.launch {
                        try {
                                _state.value = Loading(OnboardingStep.LOADING_HISTORY)
                                val rawTracks = musicService.getListeningHistory(limit = 120)

                                if (rawTracks.isEmpty()) {
                                        _state.value = Error("Не удалось загрузить треки")
                                        return@launch
                                }

                                val tracks = rawTracks.map {
                                        MusicTrack(id = it.id, name = it.name, artists = it.artists, albumName = it.albumName, imageUrl = it.imageUrl)
                                }

                                _state.value = Loading(OnboardingStep.ANALYZING_LANGUAGES)
                                val result = analyzeLanguages(tracks)

                                prefillListeningHistory(rawTracks, result.trackLanguages)

                                _state.value = Success(
                                        step = OnboardingStep.SELECT_LANGUAGES,
                                        tracks = tracks,
                                        languages = result.languages
                                )

                        } catch (e: Exception) {
                                Log.logger.e(e) { "$TAG: error during onboarding" }
                                _state.value = Error(e.message ?: "Неизвестная ошибка")
                        }
                }
        }

        private companion object {
                private const val TAG = "OnboardingScreenModel"
        }
}
