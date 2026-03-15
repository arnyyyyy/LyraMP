package com.arno.lyramp.di

import com.arno.lyramp.feature.listening_history.data.ListeningHistoryDatabase
import com.arno.lyramp.feature.listening_history.data.getDatabaseBuilder
import androidx.room.RoomDatabase
import org.koin.core.module.Module
import org.koin.core.qualifier.named
import org.koin.dsl.module

actual val listeningDatabaseModule: Module = module {
        single<RoomDatabase.Builder<ListeningHistoryDatabase>>(named("listening_history")) {
                getDatabaseBuilder()
        }
}
