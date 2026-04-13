package com.arno.lyramp.di

import com.arno.lyramp.feature.learn_words.data.LearnWordsRepository
import com.arno.lyramp.feature.lyrics.domain.SaveWordToLearnUseCase
import org.koin.dsl.module

val lyricsBridgeModule = module {
        single<SaveWordToLearnUseCase> {
                val repo = get<LearnWordsRepository>()
                SaveWordToLearnUseCase { word, translation, sourceLang, trackName, artists, lyricLine ->
                        repo.saveWord(word, translation, sourceLang, trackName, artists, lyricLine)
                }
        }
}
