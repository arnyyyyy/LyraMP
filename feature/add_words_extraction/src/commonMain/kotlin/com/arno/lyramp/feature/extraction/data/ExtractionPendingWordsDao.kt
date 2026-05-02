package com.arno.lyramp.feature.extraction.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
internal interface ExtractionPendingWordsDao {
        @Insert
        suspend fun insertAll(words: List<ExtractionPendingWordEntity>)

        @Query("SELECT * FROM extraction_pending_words ORDER BY id ASC")
        suspend fun getAll(): List<ExtractionPendingWordEntity>

        @Query("DELETE FROM extraction_pending_words")
        suspend fun deleteAll()
}
