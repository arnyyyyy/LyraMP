package com.arno.lyramp.feature.onboarding.domain

internal data class AnalysisResult(
        val languages: Map<String, Int>,
        val trackLanguages: Map<String, String>,
        val analysedTracksSize: Int,
)