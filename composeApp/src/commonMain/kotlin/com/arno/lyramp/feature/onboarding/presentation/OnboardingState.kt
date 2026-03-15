package com.arno.lyramp.feature.onboarding.presentation

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.feature.onboarding.model.OnboardingStep

internal sealed interface OnboardingState {
        data class Loading(val step: OnboardingStep) : OnboardingState
        data class Success(
                val step: OnboardingStep,
                val tracks: List<ListeningHistoryMusicTrack>,
                val languages: Map<String, Int>
        ) : OnboardingState

        data class Error(val message: String) : OnboardingState
}
