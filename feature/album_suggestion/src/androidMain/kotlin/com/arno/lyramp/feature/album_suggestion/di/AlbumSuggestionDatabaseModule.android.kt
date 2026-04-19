package com.arno.lyramp.feature.album_suggestion.di

import com.arno.lyramp.feature.album_suggestion.data.getAlbumSuggestionDatabaseBuilder
import androidx.room.RoomDatabase
import com.arno.lyramp.feature.album_suggestion.data.AlbumSuggestionDatabase
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val albumSuggestionDatabaseModule: Module = module {
        single<RoomDatabase.Builder<AlbumSuggestionDatabase>>(named("album_suggestion")) {
                getAlbumSuggestionDatabaseBuilder(androidContext())
        }
}
