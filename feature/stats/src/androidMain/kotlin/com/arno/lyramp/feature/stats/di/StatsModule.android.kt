package com.arno.lyramp.feature.stats.di

import androidx.room.RoomDatabase
import com.arno.lyramp.feature.stats.data.StatsDatabase
import com.arno.lyramp.feature.stats.data.getStatsDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val statsDatabaseModule: Module = module {
        single<RoomDatabase.Builder<StatsDatabase>>(named("stats")) {
                getStatsDatabaseBuilder(androidContext())
        }
}
