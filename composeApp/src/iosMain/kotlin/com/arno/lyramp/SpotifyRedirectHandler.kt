package com.arno.lyramp

import com.arno.lyramp.feature.authorization.presentation.spotify.handleSpotifyRedirect as KmpHandleRedirect

object SpotifyRedirectHandler {
    @Suppress("unused")
    fun handleSpotifyRedirect(url: String) {
        KmpHandleRedirect(url)
    }

    @Suppress("unused")
    fun handleSpotifyRedirectFromSwift(url: String) {
        handleSpotifyRedirect(url)
    }
}

@Suppress("unused")
fun handleSpotifyRedirect(url: String) =
    SpotifyRedirectHandler.handleSpotifyRedirect(url)

@Suppress("unused")
fun handleSpotifyRedirectFromSwift(url: String) =
    SpotifyRedirectHandler.handleSpotifyRedirectFromSwift(url)

