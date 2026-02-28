package com.arno.lyramp.feature.authorization.presentation.spotify

import androidx.core.net.toUri

actual fun handleSpotifyRedirect(url: String) {
        val code = url.toUri().getQueryParameter("code") ?: return
        SpotifyAuthHolder.emit(code)
}
