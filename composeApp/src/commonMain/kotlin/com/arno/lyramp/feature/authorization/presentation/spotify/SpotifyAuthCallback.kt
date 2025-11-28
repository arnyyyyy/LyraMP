package com.arno.lyramp.feature.authorization.presentation.spotify

fun interface SpotifyAuthCallback {
        fun onAuthCodeReceived(code: String)
}

object SpotifyAuthHolder {
        var callback: SpotifyAuthCallback? = null
}

expect fun registerSpotifyAuthCallback(callback: SpotifyAuthCallback)
expect fun handleSpotifyRedirect(url: String)
