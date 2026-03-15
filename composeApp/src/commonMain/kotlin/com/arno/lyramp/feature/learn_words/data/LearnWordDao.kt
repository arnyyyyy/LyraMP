package com.arno.lyramp.feature.learn_words.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface LearnWordDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insert(word: LearnWordEntity): Long

        @Query("SELECT * FROM learn_words ORDER BY timestamp DESC")
        fun getAllAsFlow(): Flow<List<LearnWordEntity>>

        @Query("SELECT * FROM learn_words ORDER BY timestamp DESC")
        suspend fun getAll(): List<LearnWordEntity>

        @Query("SELECT COUNT(*) FROM learn_words")
        suspend fun count(): Int

        @Query("SELECT * FROM learn_words WHERE word = :word AND sourceLang IS :sourceLang LIMIT 1")
        suspend fun findByWordAndLang(word: String, sourceLang: String?): LearnWordEntity?

        @Query("UPDATE learn_words SET sourcesJson = :sourcesJson WHERE id = :id")
        suspend fun updateSources(id: Long, sourcesJson: String)

        @Query("UPDATE learn_words SET isKnown = :isKnown WHERE id = :id")
        suspend fun updateKnown(id: Long, isKnown: Boolean)

        @Query("UPDATE learn_words SET isImportant = :isImportant WHERE id = :id")
        suspend fun updateImportance(id: Long, isImportant: Boolean)

        @Query("DELETE FROM learn_words WHERE id = :id")
        suspend fun deleteById(id: Long)

        @Query("DELETE FROM learn_words")
        suspend fun deleteAll()
}
