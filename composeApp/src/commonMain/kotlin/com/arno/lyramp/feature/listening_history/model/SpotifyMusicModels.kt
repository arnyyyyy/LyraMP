package com.arno.lyramp.feature.listening_history.model

import kotlinx.serialization.Serializable

@Serializable
internal data class SpotifySavedTracksResponse(val items: List<SavedItem>)

@Serializable
internal data class SavedItem(val track: Track)

@Serializable
internal data class Track(val name: String, val artists: List<Artist>)

@Serializable
internal data class Artist(val name: String)
