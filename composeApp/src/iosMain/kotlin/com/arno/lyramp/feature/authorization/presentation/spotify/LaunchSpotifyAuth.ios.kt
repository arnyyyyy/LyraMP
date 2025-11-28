package com.arno.lyramp.feature.authorization.presentation.spotify

import com.arno.lyramp.feature.authorization.model.SpotifyAuthConfig
import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual fun launchSpotifyAuth(codeChallenge: String) {
        val url =
                "https://accounts.spotify.com/authorize" +
                        "?client_id=${SpotifyAuthConfig.CLIENT_ID}" +
                        "&response_type=code" +
                        "&redirect_uri=${SpotifyAuthConfig.REDIRECT_URI}" +
                        "&code_challenge=$codeChallenge" +
                        "&code_challenge_method=S256" +
                        "&scope=${SpotifyAuthConfig.SCOPE}"

        UIApplication.sharedApplication.openURL(NSURL(string = url))
}
