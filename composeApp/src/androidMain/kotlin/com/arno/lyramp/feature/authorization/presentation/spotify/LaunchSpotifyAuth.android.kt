package com.arno.lyramp.feature.authorization.presentation.spotify

import android.content.Intent
import androidx.core.content.ContextCompat.startActivity
import com.arno.lyramp.MainActivity
import androidx.core.net.toUri
import com.arno.lyramp.feature.authorization.model.SpotifyAuthConfig

actual fun launchSpotifyAuth(codeChallenge: String) {
        val context = MainActivity.instance ?: return
        val url =
                "https://accounts.spotify.com/authorize" +
                        "?client_id=${SpotifyAuthConfig.CLIENT_ID}" +
                        "&response_type=code" +
                        "&redirect_uri=${SpotifyAuthConfig.REDIRECT_URI}" +
                        "&code_challenge=$codeChallenge" +
                        "&code_challenge_method=S256" +
                        "&scope=${SpotifyAuthConfig.SCOPE}"

        val intent = Intent(Intent.ACTION_VIEW, url.toUri())
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(context, intent, null)
}

