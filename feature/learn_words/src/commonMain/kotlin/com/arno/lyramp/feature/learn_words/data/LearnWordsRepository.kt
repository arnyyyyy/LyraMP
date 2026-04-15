package com.arno.lyramp.feature.learn_words.data

import kotlinx.coroutines.flow.Flow

internal class LearnWordsRepository(private val dao: LearnWordDao) {
        suspend fun saveWord(
                word: String,
                translation: String,
                sourceLang: String?,
                trackName: String,
                artists: List<String>,
                lyricLine: String
        ) {
                val newSource = WordSource(
                        lyricLine = lyricLine,
                        trackName = trackName,
                        artists = artists.joinToString(", ")
                )
                val existing = dao.findByWordAndLang(word, sourceLang)

                if (existing != null) {
                        val sources = existing.parseSources().toMutableList()
                        val alreadyExists = sources.any {
                                it.lyricLine == newSource.lyricLine && it.trackName == newSource.trackName
                        }
                        if (!alreadyExists && sources.size < 3) {
                                sources.add(newSource)
                                dao.updateSources(existing.id, LearnWordEntity.encodeSources(sources))
                        }
                } else {
                        dao.insert(
                                LearnWordEntity(
                                        word = word,
                                        translation = translation,
                                        sourceLang = sourceLang,
                                        sourcesJson = LearnWordEntity.encodeSources(listOf(newSource))
                                )
                        )
                }
        }

        fun getAllWords(): Flow<List<LearnWordEntity>> = dao.getAllAsFlow()

        suspend fun getById(id: Long): LearnWordEntity? = dao.findById(id)

        suspend fun updateProgress(id: Long, progress: Float) = dao.updateProgress(id, progress)

        suspend fun markAsKnown(id: Long, isKnown: Boolean) = dao.updateKnown(id, isKnown)

        suspend fun toggleImportance(id: Long, isImportant: Boolean) = dao.updateImportance(id, isImportant)
}
