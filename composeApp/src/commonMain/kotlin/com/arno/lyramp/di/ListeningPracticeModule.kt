package com.arno.lyramp.di

import com.arno.lyramp.feature.listening_practice.domain.ListeningPracticeUseCase
import org.koin.dsl.module

val listeningPracticeModule = module {
        single { ListeningPracticeUseCase(get(), get()) }
}
