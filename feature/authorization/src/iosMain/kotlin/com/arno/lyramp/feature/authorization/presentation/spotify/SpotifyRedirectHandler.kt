package com.arno.lyramp.feature.authorization.presentation.spotify

import platform.Foundation.NSURL

actual fun handleSpotifyRedirect(url: String) {
        val nsUrl = NSURL.URLWithString(url) ?: return
        val query = nsUrl.query ?: return

        val params = query.split("&")
                .mapNotNull {
                        val parts = it.split("=")
                        if (parts.size == 2) parts[0] to parts[1] else null
                }.toMap()

        val code = params["code"] ?: return
        SpotifyAuthHolder.emit(code)
}
