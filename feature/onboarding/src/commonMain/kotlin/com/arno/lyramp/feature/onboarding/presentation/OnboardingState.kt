package com.arno.lyramp.feature.onboarding.presentation

import com.arno.lyramp.core.model.MusicTrack

internal sealed interface OnboardingState {
        data class Loading(val step: OnboardingStep) : OnboardingState
        data class Success(
                val step: OnboardingStep,
                val tracks: List<MusicTrack>,
                val languages: Map<String, Int>,
                val analysedTracksSize: Int
        ) : OnboardingState

        data class Error(val message: String) : OnboardingState
}
