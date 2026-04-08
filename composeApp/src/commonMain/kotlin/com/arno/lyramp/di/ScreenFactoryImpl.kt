package com.arno.lyramp.di

import cafe.adriel.voyager.core.screen.Screen
import com.arno.lyramp.core.navigation.ScreenFactory
import com.arno.lyramp.feature.extraction.ui.ExtractionVoyagerScreen
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.feature.listening_practice.ui.ListeningPracticeScreen
import com.arno.lyramp.feature.lyrics.ui.LyricsScreen
import com.arno.lyramp.feature.main.ui.MainScreen
import com.arno.lyramp.feature.onboarding.ui.OnboardingScreen
import com.arno.lyramp.feature.word_suggestions.ui.WordSuggestionsVoyagerScreen

class ScreenFactoryImpl : ScreenFactory {
        override fun lyricsScreen(
                trackId: String?,
                trackName: String,
                artists: List<String>,
                albumName: String?,
                imageUrl: String?,
        ): Screen = LyricsScreen(
                trackId = trackId,
                trackName = trackName,
                artists = artists,
                albumName = albumName,
                imageUrl = imageUrl
        )

        override fun listeningPracticeScreen(
                id: String,
                albumId: String?,
                name: String,
                artists: List<String>,
                albumName: String?,
                imageUrl: String?,
        ): Screen = ListeningPracticeScreen(
                PracticeTrack(
                        id = id,
                        albumId = albumId,
                        name = name,
                        artists = artists,
                        albumName = albumName,
                        imageUrl = imageUrl
                )
        )

        override fun onboardingScreen(): Screen = OnboardingScreen

        override fun mainScreen(): Screen = MainScreen

        override fun storyGeneratorScreen(): Screen {
                // AAAA TODO
                throw NotImplementedError("Story generator screen not yet available")
        }

        override fun extractionScreen(): Screen = ExtractionVoyagerScreen // TODO

        override fun wordSuggestionsScreen(): Screen = WordSuggestionsVoyagerScreen // TODO
}

