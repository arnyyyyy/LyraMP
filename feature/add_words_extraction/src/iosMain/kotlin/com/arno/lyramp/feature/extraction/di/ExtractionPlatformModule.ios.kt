package com.arno.lyramp.feature.extraction.di

import com.arno.lyramp.feature.extraction.data.ExtractionShownDatabase
import com.arno.lyramp.feature.extraction.data.getExtractionDatabaseBuilder
import androidx.room.RoomDatabase
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val extractionPlatformModule: Module = module {
        single<RoomDatabase.Builder<ExtractionShownDatabase>>(named("extraction")) {
                getExtractionDatabaseBuilder()
        }
}
