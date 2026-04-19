package com.arno.lyramp.feature.learn_words.data

import kotlinx.coroutines.flow.Flow

internal class LearnWordsRepository(private val dao: LearnWordDao) {
        suspend fun saveWord(
                word: String,
                translation: String,
                sourceLang: String?,
                trackName: String,
                artists: List<String>,
                lyricLine: String,
                albumId: String? = null,
                trackIndex: Int? = null,
                isKnown: Boolean = false
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
                        if (existing.albumId == null && albumId != null) {
                                dao.updateAlbumInfo(existing.id, albumId, trackIndex)
                        }
                } else {
                        dao.insert(
                                LearnWordEntity(
                                        word = word,
                                        translation = translation,
                                        sourceLang = sourceLang,
                                        sourcesJson = LearnWordEntity.encodeSources(listOf(newSource)),
                                        isKnown = isKnown,
                                        albumId = albumId,
                                        trackIndex = trackIndex
                                )
                        )
                }
        }

        fun getAllWords(): Flow<List<LearnWordEntity>> = dao.getAllAsFlow()

        suspend fun getById(id: Long): LearnWordEntity? = dao.findById(id)

        suspend fun getByAlbumId(albumId: String): List<LearnWordEntity> = dao.getByAlbumId(albumId)

        fun observeByAlbumId(albumId: String): Flow<List<LearnWordEntity>> = dao.observeByAlbumId(albumId)

        suspend fun updateProgress(id: Long, progress: Float) = dao.updateProgress(id, progress)

        suspend fun markAsKnown(id: Long, isKnown: Boolean) = dao.updateKnown(id, isKnown)

        suspend fun toggleImportance(id: Long, isImportant: Boolean) = dao.updateImportance(id, isImportant)

        suspend fun incrementProgress(id: Long, step: Float = 0.2f) {
                val entity = getById(id) ?: return
                if (entity.progress >= 1.0f) return
                val newProgress = (entity.progress + step).coerceAtMost(1.0f)
                updateProgress(id, newProgress)
        }
}
