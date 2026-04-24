package com.arno.lyramp.feature.extraction.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface ExtractionShownWordsDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertAll(words: List<ExtractionShownWordsEntity>)

        @Query("SELECT word FROM extraction_shown_words WHERE language = :language")
        suspend fun getWordsForLanguage(language: String): List<String>

        @Query("SELECT word FROM extraction_shown_words WHERE language = :trackLanguage OR language = :legacyGlobal")
        suspend fun getWordsForExtraction(trackLanguage: String, legacyGlobal: String): List<String>
}
