package com.arno.lyramp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity

class DeepLinkRouterActivity : ComponentActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
                super.onCreate(savedInstanceState)

                val dataUri = intent?.data

                if (dataUri != null) {
                        val forward = Intent(this, MainActivity::class.java).apply {
                                action = Intent.ACTION_VIEW
                                data = dataUri
                                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP)
                        }
                        startActivity(forward)
                } else {
                        startActivity(Intent(this, MainActivity::class.java))
                }

                finish()
        }
}

