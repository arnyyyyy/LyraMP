//package com.arno.lyramp.feature.authorization.repository
//
//import com.arno.lyramp.feature.authorization.model.MusicServiceType
//
//internal class YandexAuthRepository : AuthPlaylistRepository {
//        override fun hasPlaylist(): Boolean = !YandexAuthStorage.playlistUrl.isNullOrBlank()
//        override fun getPlaylistUrl(): String? = YandexAuthStorage.playlistUrl
//        override fun savePlaylistUrl(url: String?) {
//                YandexAuthStorage.playlistUrl = url
//                AuthSelectionStorage.lastAuthorizedService = if (url.isNullOrBlank()) null else MusicServiceType.YANDEX.name
//        }
//}
