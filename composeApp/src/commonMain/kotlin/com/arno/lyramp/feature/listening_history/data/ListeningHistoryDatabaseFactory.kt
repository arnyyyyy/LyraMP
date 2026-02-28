package com.arno.lyramp.feature.listening_history.data

import androidx.room.RoomDatabase
import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO

fun getListeningHistoryDatabase(builder: RoomDatabase.Builder<ListeningHistoryDatabase>): ListeningHistoryDatabase {
        return builder
                .setDriver(BundledSQLiteDriver())
                .setQueryCoroutineContext(Dispatchers.IO)
                .build()
}
