package com.arno.lyramp.feature.listening_practice.di

import com.arno.lyramp.feature.listening_practice.domain.ListeningPracticeUseCase
import com.arno.lyramp.feature.listening_practice.presentation.ListeningPracticeScreenModel
import com.arno.lyramp.feature.listening_practice.model.PracticeTrack
import org.koin.dsl.module

val listeningPracticeModule = module {
        single { ListeningPracticeUseCase(get(), get()) }
        factory { (track: PracticeTrack) -> ListeningPracticeScreenModel(track, get()) }
}
