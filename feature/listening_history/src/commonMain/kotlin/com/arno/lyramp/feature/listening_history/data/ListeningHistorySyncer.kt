package com.arno.lyramp.feature.listening_history.data

import com.arno.lyramp.core.model.LyraLang.foldLatinDiacritics
import com.arno.lyramp.core.util.replaceNonLetterDigitWithSpace
import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack
import com.arno.lyramp.feature.listening_history.model.stableKey

internal class ListeningHistorySyncer(
        private val dao: ListeningHistoryDao,
) {
        suspend fun applyDiff(cached: List<ListeningHistoryTrackEntity>, fresh: List<ListeningHistoryMusicTrack>) {
                val freshIds = fresh.map { it.stableKey() }.toSet()
                val freshTitleKeys = fresh.map { it.titleArtistKey() }.toSet()
                val cachedIds = cached.map { it.stableKey() }.toSet()
                val cachedTitleKeys = cached.map { it.titleArtistKey() }.toSet()
                val hiddenIds = dao.getHiddenTrackKeys().toSet()

                backfillMissingSourceIds(fresh)

                val manualIds = cached
                        .filter { it.sourceId == null && it.trackId?.contains("||") == true }
                        .map { it.stableKey() }
                        .toSet()

                val matchedByTitleIds = cached
                        .filter { it.titleArtistKey() in freshTitleKeys }
                        .map { it.stableKey() }
                        .toSet()

                val toDelete = (cachedIds - freshIds - matchedByTitleIds) - manualIds
                if (toDelete.isNotEmpty()) {
                        val remaining = (freshIds + matchedByTitleIds + manualIds).toList()
                        if (remaining.isNotEmpty()) dao.deleteShowingNotIn(remaining)
                        else dao.deleteAllShowing()
                }

                val toInsert = fresh.filter { track ->
                        val id = track.stableKey()
                        id !in cachedIds &&
                            track.titleArtistKey() !in cachedTitleKeys &&
                            id !in hiddenIds
                }
                if (toInsert.isNotEmpty()) {
                        dao.insertAll(toInsert.reversed().map { it.toEntity() })
                }
        }

        private suspend fun backfillMissingSourceIds(fresh: List<ListeningHistoryMusicTrack>) {
                fresh.forEach { track ->
                        val sourceId = track.sourceId ?: return@forEach
                        val trackId = track.id
                        if (trackId != null) {
                                dao.backfillSourceIdByTrackId(trackId, sourceId)
                        } else {
                                dao.backfillSourceIdByTitleAndArtists(
                                        name = track.name,
                                        artists = track.artists.joinToString(","),
                                        sourceId = sourceId,
                                )
                        }
                }
        }

        private fun ListeningHistoryTrackEntity.stableKey() = trackId?.takeIf { it.isNotBlank() } ?: "$name||$artists"

        private fun ListeningHistoryMusicTrack.titleArtistKey() = "$name||${artists.joinToString(",")}".normalizedKey()

        private fun ListeningHistoryTrackEntity.titleArtistKey() = "$name||$artists".normalizedKey()

        private fun String.normalizedKey(): String =
                lowercase()
                        .foldLatinDiacritics()
                        .replaceNonLetterDigitWithSpace()
                        .replace(Regex("\\s+"), " ")
                        .trim()
}
