package com.arno.lyramp.feature.learn_words.data

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Serializable
data class WordSource(
        val lyricLine: String,
        val trackName: String,
        val artists: String
)

@Entity(
        tableName = "learn_words",
        indices = [Index(value = ["word", "sourceLang"], unique = true)]
)
data class LearnWordEntity @OptIn(ExperimentalTime::class) constructor(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,
        val word: String,
        val translation: String,
        val sourceLang: String?,
        val sourcesJson: String = "",
        val isKnown: Boolean = false,
        val isImportant: Boolean = false,
        val timestamp: Long = Clock.System.now().toEpochMilliseconds(),
        val progress: Float = 0f,
        val albumId: String? = null,
        val trackIndex: Int? = null
) {
        fun parseSources(): List<WordSource> =
                if (sourcesJson.isBlank() || sourcesJson == "[]") emptyList()
                else Json.decodeFromString(sourcesJson)

        companion object {
                fun encodeSources(sources: List<WordSource>): String =
                        Json.encodeToString(sources)
        }
}
