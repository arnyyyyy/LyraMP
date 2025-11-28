package com.arno.lyramp.feature.authorization.presentation.spotify

import androidx.core.net.toUri

actual fun registerSpotifyAuthCallback(callback: SpotifyAuthCallback) {
        SpotifyAuthHolder.callback = callback
}

actual fun handleSpotifyRedirect(url: String) {
        val uri = url.toUri()
        val code = uri.getQueryParameter("code")

        if (code != null) {
                SpotifyAuthHolder.callback?.onAuthCodeReceived(code)
        }
}
