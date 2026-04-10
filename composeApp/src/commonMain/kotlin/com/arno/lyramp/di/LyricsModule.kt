package com.arno.lyramp.di

import com.arno.lyramp.core.model.MusicTrack
import com.arno.lyramp.core.model.WordDifficultyProvider
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.lyrics.api.LyricsOvhApi
import com.arno.lyramp.feature.lyrics.api.YandexLyricsApi
import com.arno.lyramp.feature.lyrics.domain.LyricsOvhService
import com.arno.lyramp.feature.lyrics.domain.LyricsServiceFactory
import com.arno.lyramp.feature.lyrics.domain.YandexLyricsService
import com.arno.lyramp.feature.lyrics.domain.LyricsUseCase
import com.arno.lyramp.feature.lyrics.presentation.LyricsScreenModel
import com.arno.lyramp.feature.lyrics.presentation.PopupAudioManager
import com.arno.lyramp.feature.translation.domain.GetSpeechFilePathUseCase
import com.arno.lyramp.feature.translation.domain.TranslateWordWithStateUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import org.koin.dsl.module

val lyricsModule = module {
        single { LyricsOvhApi(get()) }
        single { YandexLyricsApi(get()) }

        single { YandexLyricsService(get(), get()) }
        single { LyricsOvhService(get()) }

        single { LyricsServiceFactory(get(), get()) }

        single { LyricsUseCase(get()) }

        factory { (track: MusicTrack) ->
                val repo: LearnWordsRepository = get()
                LyricsScreenModel(
                        track = track,
                        lyricsUseCase = get(),
                        translateWord = get<TranslateWordWithStateUseCase>(),
                        audioManager = PopupAudioManager(getSpeechFilePath = get<GetSpeechFilePathUseCase>()),
                        saveWordToLearn = { word, translation, sourceLang, trackName, artists, lyricLine ->
                                repo.saveWord(word, translation, sourceLang, trackName, artists, lyricLine)
                        },
                        wordDifficultyProvider = get<WordDifficultyProvider>(),
                        getSelectedLanguage = get<GetSelectedLanguageUseCase>(),
                )
        }
}
