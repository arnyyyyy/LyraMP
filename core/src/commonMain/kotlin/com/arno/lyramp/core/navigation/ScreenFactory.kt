package com.arno.lyramp.core.navigation

import cafe.adriel.voyager.core.screen.Screen

interface ScreenFactory {
        fun onboardingScreen(): Screen

        fun mainScreen(): Screen

        fun storyGeneratorScreen(): Screen

        fun extractionScreen(): Screen

        fun albumLearningScreen(albumId: String): Screen

        fun albumSelectorScreen(): Screen

        fun learnWordsScreenForTrack(modeName: String, albumId: String, trackIndex: Int): Screen

        fun allWordsListScreen(language: String?): Screen

        fun statsScreen(language: String?): Screen

        fun statsVocabularyScreen(language: String, statusName: String): Screen

        fun statsCefrWordsScreen(language: String, groupName: String): Screen

        fun auditionScreen(language: String?): Screen

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
}
