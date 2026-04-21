package com.arno.lyramp.feature.stories_generator.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "generated_stories", indices = [Index(value = ["wordsHash"], unique = true)])
internal data class GeneratedStoryEntity(
        @PrimaryKey(autoGenerate = true) val id: Long = 0L,
        val title: String,
        val genre: String,
        val wordsJson: String,
        val content: String,
        val language: String,
        val createdAt: Long,
        val wordsHash: String,
        val isRead: Boolean = false,
        val isManual: Boolean = false
)
