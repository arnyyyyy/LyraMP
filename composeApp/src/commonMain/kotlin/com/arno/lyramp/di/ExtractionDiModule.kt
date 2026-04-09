package com.arno.lyramp.di

import com.arno.lyramp.feature.extraction.domain.usecase.SaveWordUseCase
import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import org.koin.dsl.module

val extractionDiModule = module { // TODO поправить
        single<SaveWordUseCase> {
                val repo = get<LearnWordsRepository>()
                SaveWordUseCase { word, translation, sourceLang, trackName, artists, lyricLine ->
                        repo.saveWord(word, translation, sourceLang, trackName, artists, lyricLine)
                }
        }
}
