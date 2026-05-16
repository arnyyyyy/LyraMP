package com.arno.lyramp.di

import cafe.adriel.voyager.core.screen.Screen
import com.arno.lyramp.core.navigation.ScreenFactory
import com.arno.lyramp.feature.extraction.ui.ExtractionVoyagerScreen
import com.arno.lyramp.feature.album_suggestion.ui.AlbumLearningScreen
import com.arno.lyramp.feature.album_suggestion.ui.AlbumSelectorScreen
import com.arno.lyramp.feature.listening_practice.ui.AuditionScreen
import com.arno.lyramp.feature.listening_practice.ui.ListeningPracticeScreen
import com.arno.lyramp.feature.lyrics.ui.LyricsScreen
import com.arno.lyramp.feature.main.ui.MainScreen
import com.arno.lyramp.feature.onboarding.ui.OnboardingScreen
import com.arno.lyramp.feature.stories_generator.ui.StoriesCatalogScreen
import com.arno.lyramp.feature.learn_words.navigation.LearnWordsModeArg
import com.arno.lyramp.feature.learn_words.navigation.learnWordsScreenForTrack as learnWordsScreenForTrackFactory
import com.arno.lyramp.feature.learn_words.ui.AllWordsListScreen
import com.arno.lyramp.feature.stats.ui.StatsCefrWordsScreen
import com.arno.lyramp.feature.stats.ui.StatsScreen
import com.arno.lyramp.feature.stats.ui.StatsVocabularyScreen

class ScreenFactoryImpl : ScreenFactory {
        override fun onboardingScreen(): Screen = OnboardingScreen

        override fun mainScreen(): Screen = MainScreen

        override fun storyGeneratorScreen(): Screen = StoriesCatalogScreen

        override fun extractionScreen(): Screen = ExtractionVoyagerScreen

        override fun albumLearningScreen(albumId: String): Screen = AlbumLearningScreen(albumId)

        override fun albumSelectorScreen(): Screen = AlbumSelectorScreen

        override fun learnWordsScreenForTrack(modeName: String, albumId: String, trackIndex: Int): Screen =
                learnWordsScreenForTrackFactory(
                        mode = LearnWordsModeArg.valueOf(modeName),
                        albumId = albumId,
                        trackIndex = trackIndex
                )

        override fun allWordsListScreen(language: String?): Screen = AllWordsListScreen(language = language)

        override fun statsScreen(language: String?): Screen = StatsScreen(language = language)

        override fun statsVocabularyScreen(language: String, statusName: String): Screen =
                StatsVocabularyScreen(language = language, statusName = statusName)

        override fun statsCefrWordsScreen(language: String, groupName: String): Screen =
                StatsCefrWordsScreen(language = language, groupName = groupName)

        override fun auditionScreen(language: String?): Screen = AuditionScreen(language)

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
                trackId = id,
                albumId = albumId,
                trackName = name,
                artists = artists,
                albumName = albumName,
                imageUrl = imageUrl,
        )
}
