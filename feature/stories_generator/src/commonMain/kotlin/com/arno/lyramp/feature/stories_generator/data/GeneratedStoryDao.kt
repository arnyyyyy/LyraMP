package com.arno.lyramp.feature.stories_generator.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
internal interface GeneratedStoryDao {
        @Insert(onConflict = OnConflictStrategy.IGNORE)
        suspend fun insert(story: GeneratedStoryEntity): Long

        @Query("SELECT * FROM generated_stories ORDER BY createdAt DESC")
        fun observeAll(): Flow<List<GeneratedStoryEntity>>

        @Query("SELECT * FROM generated_stories WHERE id = :id LIMIT 1")
        suspend fun findById(id: Long): GeneratedStoryEntity?

        @Query("SELECT EXISTS(SELECT 1 FROM generated_stories WHERE wordsHash = :hash)")
        suspend fun existsByHash(hash: String): Boolean

        @Query("SELECT COUNT(*) FROM generated_stories")
        suspend fun count(): Int

        @Query("SELECT COUNT(*) FROM generated_stories WHERE isRead = 0")
        suspend fun countUnread(): Int

        @Query("UPDATE generated_stories SET isRead = 1 WHERE id = :id")
        suspend fun markAsRead(id: Long)

        @Query("DELETE FROM generated_stories WHERE id = :id")
        suspend fun deleteById(id: Long)

        @Query(
                "DELETE FROM generated_stories WHERE id IN (" +
                    "SELECT id FROM generated_stories ORDER BY createdAt DESC LIMIT -1 OFFSET :keep" +
                    ")"
        )
        suspend fun trimToSize(keep: Int) // todo? спросить, может лучше вручную удалять
}
