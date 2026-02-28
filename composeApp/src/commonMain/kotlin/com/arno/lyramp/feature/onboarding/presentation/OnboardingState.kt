package com.arno.lyramp.feature.onboarding.presentation

import com.arno.lyramp.feature.listening_history.model.MusicTrack
import com.arno.lyramp.feature.onboarding.model.OnboardingStep

internal sealed interface OnboardingState {
        data class Loading(val step: OnboardingStep) : OnboardingState
        data class Success(
                val step: OnboardingStep,
                val tracks: List<MusicTrack>,
                val languages: Map<String, Int>
        ) : OnboardingState

        data class Error(val message: String) : OnboardingState
}
