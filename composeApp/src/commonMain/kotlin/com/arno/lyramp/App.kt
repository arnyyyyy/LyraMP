package com.arno.lyramp

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.arno.lyramp.core.LyraApp
import com.arno.lyramp.di.appModules
import org.koin.compose.KoinApplication

@Composable
fun App() {
        KoinApplication(application = {
                modules(appModules)
        }) {
                MaterialTheme {
                        LyraApp()
                }
        }
}
