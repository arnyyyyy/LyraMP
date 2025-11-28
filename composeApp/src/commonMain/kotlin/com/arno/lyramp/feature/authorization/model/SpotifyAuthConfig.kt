package com.arno.lyramp.feature.authorization.model

expect object SpotifyAuthConfig {
        val CLIENT_ID: String
        val REDIRECT_URI: String
        val SCOPE: String
}