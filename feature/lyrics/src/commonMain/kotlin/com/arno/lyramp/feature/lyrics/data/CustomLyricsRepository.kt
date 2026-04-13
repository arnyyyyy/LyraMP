package com.arno.lyramp.feature.lyrics.data

internal class CustomLyricsRepository(private val dao: CustomLyricsDao) {
        suspend fun getCustomLyrics(artist: String, song: String): String? {
                return dao.getLyrics(getId(artist, song))
        }

        suspend fun saveCustomLyrics(artist: String, song: String, lyrics: String) {
                dao.saveLyrics(CustomLyricsEntity(id = getId(artist, song), lyrics = lyrics))
        }

        private fun getId(artist: String, song: String): String {
                return "${artist.trim().lowercase()}_${song.trim().lowercase()}"
        } // TODO вообще перестроить, что у всех сервисов будет id-шник, просто в яндексе он будет по трекайди а у остальных как тут
        // тогда никаких гетайди
}
