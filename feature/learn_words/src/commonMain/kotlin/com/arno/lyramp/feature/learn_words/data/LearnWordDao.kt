package com.arno.lyramp.feature.learn_words.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface LearnWordDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insert(word: LearnWordEntity): Long

        @Query("SELECT * FROM learn_words ORDER BY timestamp DESC")
        fun getAllAsFlow(): Flow<List<LearnWordEntity>>

        @Query("SELECT * FROM learn_words ORDER BY timestamp DESC")
        suspend fun getAll(): List<LearnWordEntity>

        @Query("SELECT COUNT(*) FROM learn_words")
        suspend fun count(): Int

        @Query("SELECT * FROM learn_words WHERE id = :id LIMIT 1")
        suspend fun findById(id: Long): LearnWordEntity?

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

        @Query("UPDATE learn_words SET progress = :progress WHERE id = :id")
        suspend fun updateProgress(id: Long, progress: Float)

        @Query("SELECT * FROM learn_words WHERE albumId = :albumId ORDER BY trackIndex ASC, word ASC")
        suspend fun getByAlbumId(albumId: String): List<LearnWordEntity>

        @Query("SELECT * FROM learn_words WHERE albumId = :albumId ORDER BY trackIndex ASC, word ASC")
        fun observeByAlbumId(albumId: String): Flow<List<LearnWordEntity>>

        @Query("SELECT * FROM learn_words WHERE albumId = :albumId AND trackIndex = :trackIndex ORDER BY word ASC")
        suspend fun getByAlbumIdAndTrackIndex(albumId: String, trackIndex: Int): List<LearnWordEntity>

        @Query("SELECT COUNT(*) FROM learn_words WHERE albumId = :albumId AND trackIndex = :trackIndex AND progress >= 1.0")
        suspend fun countLearnedByAlbumAndTrack(albumId: String, trackIndex: Int): Int

        @Query("SELECT word FROM learn_words WHERE sourceLang IS :sourceLang AND isKnown = 1")
        suspend fun getKnownWords(sourceLang: String?): List<String>

        @Query("SELECT word FROM learn_words WHERE sourceLang IS :sourceLang")
        suspend fun getAllWordStrings(sourceLang: String?): List<String>

        @Query("UPDATE learn_words SET albumId = :albumId, trackIndex = :trackIndex WHERE id = :id")
        suspend fun updateAlbumInfo(id: Long, albumId: String, trackIndex: Int?)
}
