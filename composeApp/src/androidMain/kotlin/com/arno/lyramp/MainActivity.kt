package com.arno.lyramp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.arno.lyramp.feature.authorization.presentation.spotify.handleSpotifyRedirect

class MainActivity : ComponentActivity() {
        companion object {
                var instance: MainActivity? = null
        }

        override fun onCreate(savedInstanceState: Bundle?) {
                instance = this
                enableEdgeToEdge()
                super.onCreate(savedInstanceState)

                intent?.data?.toString()?.let { deepLink ->
                        handleSpotifyRedirect(deepLink)
                }

                setContent {
                        App()
                }
        }

        override fun onNewIntent(intent: Intent) {
                super.onNewIntent(intent)
                setIntent(intent)
                intent.data?.toString()?.let { deepLink ->
                        handleSpotifyRedirect(deepLink)
                }
        }

        override fun onDestroy() {
                instance = null
                super.onDestroy()
        }
}