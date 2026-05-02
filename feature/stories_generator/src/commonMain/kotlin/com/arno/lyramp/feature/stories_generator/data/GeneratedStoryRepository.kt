package com.arno.lyramp.feature.stories_generator.data

import com.arno.lyramp.feature.stories_generator.model.GeneratedStory
import com.arno.lyramp.feature.stories_generator.model.StoryGenre
import com.arno.lyramp.feature.stories_generator.model.StoryWord
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.json.Json

internal class GeneratedStoryRepository(
        private val dao: GeneratedStoryDao
) {
        private val json = Json { ignoreUnknownKeys = true }

        fun observeByLanguage(language: String) = dao.observeByLanguage(language).map { list -> list.map { it.toDomain() } }

        suspend fun getById(id: Long) = dao.findById(id)?.toDomain()
        suspend fun countUnreadByLanguage(language: String) = dao.countUnreadByLanguage(language)
        suspend fun markAsRead(id: Long) = dao.markAsRead(id)

        suspend fun save(story: GeneratedStory, isManual: Boolean = false): Long {
                val hash = computeHash(story.wordsUsed, story.genre, story.language)
                if (dao.existsByHash(hash)) return -1L
                val entity = GeneratedStoryEntity(
                        title = story.title,
                        genre = story.genre.name,
                        wordsJson = json.encodeToString(ListSerializer(StoryWord.serializer()), story.wordsUsed),
                        content = story.text,
                        language = story.language,
                        createdAt = story.createdAt,
                        wordsHash = hash,
                        isRead = isManual,
                        isManual = isManual
                )
                return dao.insert(entity)
        }

        suspend fun trimToSizeByLanguage(language: String, keep: Int) = dao.trimToSizeByLanguage(language, keep)
        suspend fun deleteStory(id: Long) = dao.deleteById(id)
        suspend fun wouldBeDuplicate(
                words: List<StoryWord>,
                genre: StoryGenre,
                language: String
        ) = dao.existsByHash(computeHash(words, genre, language))

        private fun GeneratedStoryEntity.toDomain(): GeneratedStory = GeneratedStory(
                id = id,
                title = title,
                genre = runCatching { StoryGenre.valueOf(genre) }.getOrDefault(StoryGenre.DRAMA),
                text = content,
                wordsUsed = runCatching {
                        json.decodeFromString(ListSerializer(StoryWord.serializer()), wordsJson)
                }.getOrDefault(emptyList()),
                language = language,
                createdAt = createdAt,
                isRead = isRead
        )

        private fun computeHash(
                words: List<StoryWord>,
                genre: StoryGenre,
                language: String
        ): String {
                val sortedWords = words.map { it.word.lowercase() }.sorted().joinToString("|")
                return "${genre.name}|$language|$sortedWords"
        }
}
