package com.arno.lyramp.feature.extraction.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
internal interface ExtractionTrackStatusDao {
        @Query("SELECT * FROM extraction_track_status WHERE trackId = :trackId")
        suspend fun getStatus(trackId: String): ExtractionTrackStatusEntity?

        @Query("SELECT trackId FROM extraction_track_status WHERE exhaustedLevels LIKE '%' || :levelsKey || '%'")
        suspend fun getExhaustedTrackIds(levelsKey: String): List<String>

        @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun upsert(entity: ExtractionTrackStatusEntity)
}
