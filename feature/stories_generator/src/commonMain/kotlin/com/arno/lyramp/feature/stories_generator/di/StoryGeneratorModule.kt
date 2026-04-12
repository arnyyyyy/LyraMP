package com.arno.lyramp.feature.stories_generator.di

import com.arno.lyramp.feature.stories_generator.domain.ModelDownloadRepository
import com.arno.lyramp.feature.stories_generator.presentation.StoryScreenModel
import org.koin.dsl.module

val storyGeneratorModule = module {
        single { ModelDownloadRepository() }
        factory {
                StoryScreenModel(
                        learnWordsRepository = get(),
                        modelDownloadRepository = get(),
                        getSelectedLanguageUseCase = get()
                )
        }
}
