package com.arno.lyramp.core.navigation

import cafe.adriel.voyager.core.screen.Screen

interface ScreenFactory {
        fun lyricsScreen(
                trackId: String?,
                trackName: String,
                artists: List<String>,
                albumName: String?,
                imageUrl: String?,
        ): Screen

        fun listeningPracticeScreen(
                id: String,
                albumId: String?,
                name: String,
                artists: List<String>,
                albumName: String?,
                imageUrl: String?,
        ): Screen

        fun onboardingScreen(): Screen

        fun mainScreen(): Screen

        fun storyGeneratorScreen(): Screen

        fun extractionScreen(): Screen

        fun wordSuggestionsScreen(): Screen
}
