package com.arno.lyramp.feature.album_suggestion.data

import com.arno.lyramp.feature.album_suggestion.domain.model.toEntity
import com.arno.lyramp.feature.album_suggestion.domain.model.toInfo

internal class AlbumSuggestionRepository(
        private val albumProgressDao: AlbumProgressDao,
        private val candidateDao: ExtractionCandidateDao
) {
        suspend fun getAlbumProgress(albumId: String) = albumProgressDao.getByAlbumId(albumId)?.toInfo()

        suspend fun getAlbumProgressBatch(albumIds: List<String>): Map<String, AlbumProgressInfo> =
                albumProgressDao.getByAlbumIds(albumIds).associate { it.albumId to it.toInfo() }

        suspend fun upsertAlbumProgress(info: AlbumProgressInfo) = albumProgressDao.upsert(info.toEntity())

        suspend fun getCandidatesByTrack(albumId: String, trackIndex: Int) = candidateDao.getByAlbumAndTrack(albumId, trackIndex)

        suspend fun getCandidatesByAlbum(albumId: String) = candidateDao.getByAlbum(albumId)

        fun observeCandidatesByAlbum(albumId: String) = candidateDao.observeByAlbum(albumId)

        suspend fun saveCandidates(candidates: List<ExtractionCandidateEntity>) {
                if (candidates.isNotEmpty()) candidateDao.insertAll(candidates)
        }

        suspend fun removeCandidateWords(albumId: String, words: List<String>) {
                if (words.isNotEmpty()) candidateDao.deleteWordsByAlbum(albumId, words)
        }

        suspend fun removeAllCandidatesForAlbum(albumId: String) = candidateDao.deleteByAlbum(albumId)
}
