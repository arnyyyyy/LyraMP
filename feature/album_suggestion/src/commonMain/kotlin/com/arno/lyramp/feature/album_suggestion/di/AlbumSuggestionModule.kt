package com.arno.lyramp.feature.album_suggestion.di


import com.arno.lyramp.feature.album_suggestion.data.AlbumSuggestionDatabase
import com.arno.lyramp.feature.album_suggestion.data.AlbumSuggestionRepository
import com.arno.lyramp.feature.album_suggestion.data.getAlbumSuggestionDatabase
import com.arno.lyramp.feature.album_suggestion.domain.model.CandidateMapper
import com.arno.lyramp.feature.album_suggestion.domain.usecase.EnsureAlbumExtractedUseCase
import com.arno.lyramp.feature.album_suggestion.domain.usecase.ExtractAlbumWordsUseCase
import com.arno.lyramp.feature.album_suggestion.domain.usecase.ExtractAndSaveTrackUseCase
import com.arno.lyramp.feature.album_suggestion.domain.usecase.SaveReviewedWordsUseCase
import com.arno.lyramp.feature.album_suggestion.presentation.AlbumSelectorScreenModel
import com.arno.lyramp.feature.album_suggestion.presentation.AlbumSuggestionScreenModel
import com.arno.lyramp.feature.extraction.data.CefrRepository
import com.arno.lyramp.feature.extraction.domain.usecase.MarkWordStringsAsShownUseCase
import com.arno.lyramp.feature.learn_words.domain.usecase.ObserveLearnWordsByAlbumUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetAlbumWithTracksUseCase
import com.arno.lyramp.feature.listening_history.domain.usecase.GetSuggestedAlbumsUseCase
import com.arno.lyramp.feature.lyrics.domain.GetLyricsUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLanguageSettingsUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetLearningLanguagesUseCase
import com.arno.lyramp.feature.user_settings.domain.usecase.GetSelectedLanguageUseCase
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

val albumSuggestionModule = module {

        single<AlbumSuggestionDatabase> { getAlbumSuggestionDatabase(get(named("album_suggestion"))) }
        single {
                AlbumSuggestionRepository(
                        albumProgressDao = get<AlbumSuggestionDatabase>().albumProgressDao(),
                        candidateDao = get<AlbumSuggestionDatabase>().extractionCandidateDao()
                )
        }

        single {
                ExtractAlbumWordsUseCase(
                        getLyrics = get<GetLyricsUseCase>(),
                        cefrRepository = get<CefrRepository>()
                )
        }

        single { CandidateMapper(cefrRepository = get<CefrRepository>()) }

        single {
                ExtractAndSaveTrackUseCase(
                        extractAlbumWords = get(),
                        repository = get(),
                        candidateMapper = get()
                )
        }

        single {
                EnsureAlbumExtractedUseCase(
                        repository = get(),
                        extractAndSaveTrack = get(),
                        getAllUserWords = get(),
                        getShownWords = get()
                )
        }

        single {
                SaveReviewedWordsUseCase(
                        saveLearnWord = get(),
                        repository = get(),
                        markWordsAsShown = get<MarkWordStringsAsShownUseCase>()
                )
        }

        factory {
                AlbumSelectorScreenModel(
                        getSuggestedAlbums = get<GetSuggestedAlbumsUseCase>(),
                        repository = get(),
                        getLearningLanguages = get<GetLearningLanguagesUseCase>(),
                        getSelectedLanguage = get<GetSelectedLanguageUseCase>()
                )
        }

        factory { (albumId: String) ->
                AlbumSuggestionScreenModel(
                        albumId = albumId,
                        getAlbumWithTracks = get<GetAlbumWithTracksUseCase>(),
                        getLanguageSettings = get<GetLanguageSettingsUseCase>(),
                        ensureAlbumExtracted = get(),
                        extractAndSaveTrack = get(),
                        repository = get(),
                        saveReviewedWords = get(),
                        getAllUserWords = get(),
                        observeLearnWordsByAlbum = get<ObserveLearnWordsByAlbumUseCase>(),
                        candidateMapper = get()
                )
        }
}

expect val albumSuggestionDatabaseModule: Module
