package com.arno.lyramp.feature.listening_history.di

import androidx.room.RoomDatabase
import com.arno.lyramp.feature.listening_history.data.ListeningHistoryDatabase
import com.arno.lyramp.feature.listening_history.data.getDatabaseBuilder
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val listeningHistoryPlatformModule: Module = module {
        single<RoomDatabase.Builder<ListeningHistoryDatabase>>(named("listening_history")) {
                getDatabaseBuilder(androidContext())
        }
}
