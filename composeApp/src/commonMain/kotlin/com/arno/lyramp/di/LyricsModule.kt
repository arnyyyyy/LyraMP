package com.arno.lyramp.di

import com.arno.lyramp.feature.learn_words.domain.usecase.SaveLearnWordUseCase
import com.arno.lyramp.feature.lyrics.domain.SaveWordToLearnUseCase
import org.koin.dsl.module

val lyricsBridgeModule = module {
        single<SaveWordToLearnUseCase> {
                val save = get<SaveLearnWordUseCase>()
                SaveWordToLearnUseCase { word, translation, sourceLang, trackName, artists, lyricLine ->
                        save(word, translation, sourceLang, trackName, artists, lyricLine)
                }
        }
}
