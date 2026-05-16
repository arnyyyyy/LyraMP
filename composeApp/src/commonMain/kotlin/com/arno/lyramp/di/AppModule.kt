package com.arno.lyramp.di

import com.arno.lyramp.core.navigation.ScreenFactory
import com.arno.lyramp.feature.album_suggestion.di.albumSuggestionModule
import com.arno.lyramp.feature.authorization.di.authModule
import com.arno.lyramp.feature.authorization.di.secureStorageModule
import com.arno.lyramp.feature.extraction.di.extractionModule
import com.arno.lyramp.feature.extraction.di.extractionPlatformModule
import com.arno.lyramp.feature.learn_words.di.learnWordsDatabaseModule
import com.arno.lyramp.feature.learn_words.di.learnWordsModule
import com.arno.lyramp.feature.listening_history.di.listeningHistoryModule
import com.arno.lyramp.feature.listening_history.di.listeningHistoryPlatformModule
import com.arno.lyramp.feature.listening_practice.di.listeningPracticeModule
import com.arno.lyramp.feature.lyrics.di.lyricsModule
import com.arno.lyramp.feature.lyrics.di.lyricsPlatformModule
import com.arno.lyramp.feature.music_streaming.di.musicStreamingModule
import com.arno.lyramp.feature.onboarding.di.onboardingModule
import com.arno.lyramp.feature.stats.di.statsDatabaseModule
import com.arno.lyramp.feature.stats.di.statsModule
import com.arno.lyramp.feature.stories_generator.di.generatedStoriesDatabaseModule
import com.arno.lyramp.feature.stories_generator.di.storyGeneratorModule
import com.arno.lyramp.feature.album_suggestion.di.albumSuggestionDatabaseModule
import com.arno.lyramp.feature.translation.di.translationModule
import com.arno.lyramp.feature.user_settings.di.userSettingsModule
import org.koin.dsl.module

val navigationModule = module {
        single<ScreenFactory> { ScreenFactoryImpl() }
}

val appModules = listOf(
        navigationModule,
        secureStorageModule,
        networkModule,
        authModule,
        listeningHistoryPlatformModule,
        listeningHistoryModule,
        musicStreamingModule,
        lyricsPlatformModule,
        lyricsModule,
        lyricsBridgeModule,
        translationModule,
        onboardingModule,
        listeningPracticeModule,
        auditionBridgeModule,
        learnWordsDatabaseModule,
        learnWordsModule,
        albumSuggestionDatabaseModule,
        albumSuggestionModule,
        generatedStoriesDatabaseModule,
        storyGeneratorModule,
        extractionPlatformModule,
        extractionModule,
        userSettingsModule,
        statsDatabaseModule,
        statsModule,
)
