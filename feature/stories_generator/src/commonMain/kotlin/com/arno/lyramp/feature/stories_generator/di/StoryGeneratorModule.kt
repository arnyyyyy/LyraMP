package com.arno.lyramp.feature.stories_generator.di

import com.arno.lyramp.core.background.BackgroundTaskRegistry
import com.arno.lyramp.feature.learn_words.domain.usecase.GetAllLearnWordsUseCase
import com.arno.lyramp.feature.stories_generator.background.StoryCatalogBackgroundTask
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoriesDatabase
import com.arno.lyramp.feature.stories_generator.data.GeneratedStoryRepository
import com.arno.lyramp.feature.stories_generator.data.getGeneratedStoriesDatabase
import com.arno.lyramp.feature.stories_generator.domain.StoryGenerator
import com.arno.lyramp.feature.stories_generator.domain.ModelDownloadService
import com.arno.lyramp.feature.stories_generator.domain.ModelDownloadRepository
import com.arno.lyramp.feature.stories_generator.domain.StoryGenerationService
import com.arno.lyramp.feature.stories_generator.presentation.StoriesCatalogScreenModel
import com.arno.lyramp.feature.stories_generator.presentation.StoryDetailScreenModel
import com.arno.lyramp.feature.stories_generator.presentation.StoryScreenModel
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.ObserveSelectedLanguageUseCase
import org.koin.core.qualifier.named
import org.koin.dsl.module

val storyGeneratorModule = module {
        single<ModelDownloadRepository> { ModelDownloadRepository() }
        single { StoryGenerator() }
        single { ModelDownloadService(repository = get(), generator = get()) }

        single<GeneratedStoriesDatabase> { getGeneratedStoriesDatabase(get(named("generated_stories"))) }
        single { get<GeneratedStoriesDatabase>().generatedStoryDao() }
        single { GeneratedStoryRepository(dao = get()) }

        single {
                StoryGenerationService(
                        getAllLearnWords = get<GetAllLearnWordsUseCase>(),
                        modelDownloadRepository = get(),
                        getSelectedLanguage = get<GetSelectedLanguageUseCase>(),
                        repository = get(),
                        generator = get(),
                )
        }

        factory {
                StoryScreenModel(
                        getAllLearnWords = get(),
                        downloadCoordinator = get(),
                        getSelectedLanguageUseCase = get(),
                        generationService = get(),
                )
        }

        factory {
                StoriesCatalogScreenModel(
                        repository = get(),
                        generationService = get(),
                        downloadCoordinator = get(),
                        getAllLearnWords = get<GetAllLearnWordsUseCase>(),
                        getSelectedLanguage = get<GetSelectedLanguageUseCase>(),
                        observeSelectedLanguage = get<ObserveSelectedLanguageUseCase>(),
                )
        }

        factory { (storyId: Long) ->
                StoryDetailScreenModel(
                        storyId = storyId,
                        repository = get()
                )
        }

        BackgroundTaskRegistry.register(StoryCatalogBackgroundTask.TASK_ID) { koin ->
                StoryCatalogBackgroundTask(
                        repository = koin.get<GeneratedStoryRepository>(),
                        generationService = koin.get<StoryGenerationService>(),
                        getSelectedLanguage = koin.get<GetSelectedLanguageUseCase>(),
                )
        }
}
