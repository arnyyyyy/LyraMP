package com.arno.lyramp.feature.listening_history.domain.service

import com.arno.lyramp.feature.listening_history.model.ListeningHistoryMusicTrack

/* NB: null-object
    Нужен для случая, когда пользователь не авторизован в Яндексе и не добавил ни одного плейлиста
 */
internal class EmptyMusicService : MusicService {
        override suspend fun getListeningHistory(limit: Int?): List<ListeningHistoryMusicTrack> = emptyList()
}
