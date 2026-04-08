package com.arno.lyramp.feature.listening_history.model

import kotlinx.serialization.Serializable

@Serializable
data class SpotifySavedTracksResponse(val items: List<SavedItem>) {
        @Serializable
        data class SavedItem(val track: Track) {
                @Serializable
                data class Track(val name: String, val artists: List<Artist>) {
                        @Serializable
                        data class Artist(val name: String)
                }
        }
}