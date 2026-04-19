package com.arno.lyramp.feature.extraction.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "extraction_track_status")
internal data class ExtractionTrackStatusEntity(
        @PrimaryKey val trackId: String,
        val trackName: String = "",
        val exhaustedLevels: String = "",
) {
        fun withExhaustedLevel(levelsKey: String): ExtractionTrackStatusEntity {
                val existing = exhaustedLevels.split(",").filter { it.isNotBlank() }.toMutableSet()
                existing.add(levelsKey)
                return copy(exhaustedLevels = existing.joinToString(","))
        }
}
