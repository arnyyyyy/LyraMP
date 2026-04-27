package com.arno.lyramp.feature.listening_practice.di

import com.arno.lyramp.feature.listening_practice.domain.AuditionLinePickerUseCase
import com.arno.lyramp.feature.listening_practice.domain.AuditionLinePrefetcher
import com.arno.lyramp.feature.listening_practice.domain.CheckAnswerUseCase
import com.arno.lyramp.feature.listening_practice.domain.LoadPracticeDataUseCase
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import com.arno.lyramp.feature.listening_practice.playback.LinePlaybackController
import com.arno.lyramp.feature.listening_practice.presentation.AuditionScreenModel
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeScreenModel
import org.koin.dsl.module

val listeningPracticeModule = module {
        single { LoadPracticeDataUseCase(get(), get()) }
        single { AuditionLinePickerUseCase(get(), get(), get()) }
        single { CheckAnswerUseCase() }

        factory { LinePlaybackController() }
        factory { AuditionLinePrefetcher(picker = get()) }

        factory { (track: PracticeTrack) ->
                ListeningPracticeScreenModel(
                        track = track,
                        loadPracticeData = get(),
                        playback = get(),
                        checkAnswer = get(),
                )
        }
        factory { (language: String?) ->
                AuditionScreenModel(
                        prefetcher = get(),
                        playback = get(),
                        checkAnswer = get(),
                        language = language,
                )
        }
}
