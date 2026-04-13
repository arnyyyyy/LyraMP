package com.arno.lyramp.feature.lyrics.di

import androidx.room.RoomDatabase
import com.arno.lyramp.feature.lyrics.data.CustomLyricsDatabase
import com.arno.lyramp.feature.lyrics.data.getCustomLyricsDatabaseBuilder
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val lyricsPlatformModule: Module = module {
        single<RoomDatabase.Builder<CustomLyricsDatabase>>(named("custom_lyrics")) {
                getCustomLyricsDatabaseBuilder()
        }
}
