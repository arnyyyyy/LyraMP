package com.arno.lyramp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arno.lyramp.feature.authorization.presentation.spotify.handleSpotifyRedirect

//TODO: лучше разобраться с перехватом диплинков
class MainActivity : ComponentActivity() {

        override fun onCreate(savedInstanceState: Bundle?) {
                enableEdgeToEdge()
                super.onCreate(savedInstanceState)

                intent?.data?.toString()?.let { deepLink ->
                        handleSpotifyRedirect(deepLink)
                }

                setContent { App() }
        }

        override fun onNewIntent(intent: Intent) {
                super.onNewIntent(intent)
                setIntent(intent)
                this.intent.data?.toString()?.let { deepLink ->
                        handleSpotifyRedirect(deepLink)
                }
        }
}
