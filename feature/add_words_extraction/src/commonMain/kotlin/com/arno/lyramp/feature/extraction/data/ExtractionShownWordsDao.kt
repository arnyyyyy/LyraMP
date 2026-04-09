package com.arno.lyramp.feature.extraction.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface ExtractionShownWordsDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertAll(words: List<ExtractionShownWordsEntity>)

        @Query("SELECT word FROM extraction_shown_words")
        suspend fun getAllShownWords(): List<String>

//        @Query("SELECT EXISTS(SELECT 1 FROM extraction_shown_words WHERE word = :word)")
//        suspend fun checkIfWordShown(word: String): Boolean {
//                return getAllShownWords().contains(word)
//        }
}
