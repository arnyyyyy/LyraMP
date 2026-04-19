package com.arno.lyramp.feature.album_suggestion.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface ExtractionCandidateDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insertAll(candidates: List<ExtractionCandidateEntity>)

        @Query("SELECT * FROM extraction_candidates WHERE albumId = :albumId AND trackIndex = :trackIndex ORDER BY word ASC")
        suspend fun getByAlbumAndTrack(albumId: String, trackIndex: Int): List<ExtractionCandidateEntity>

        @Query("SELECT * FROM extraction_candidates WHERE albumId = :albumId ORDER BY trackIndex ASC, word ASC")
        suspend fun getByAlbum(albumId: String): List<ExtractionCandidateEntity>

        @Query("SELECT * FROM extraction_candidates WHERE albumId = :albumId ORDER BY trackIndex ASC, word ASC")
        fun observeByAlbum(albumId: String): Flow<List<ExtractionCandidateEntity>>

        @Query("DELETE FROM extraction_candidates WHERE albumId = :albumId AND trackIndex = :trackIndex AND word IN (:words)")
        suspend fun deleteByWords(albumId: String, trackIndex: Int, words: List<String>)

        @Query("DELETE FROM extraction_candidates WHERE albumId = :albumId AND word IN (:words)")
        suspend fun deleteWordsByAlbum(albumId: String, words: List<String>)

        @Query("DELETE FROM extraction_candidates WHERE albumId = :albumId")
        suspend fun deleteByAlbum(albumId: String)
}
