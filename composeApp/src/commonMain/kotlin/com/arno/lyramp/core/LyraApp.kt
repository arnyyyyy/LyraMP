package com.arno.lyramp.core

import androidx.compose.runtime.Composable
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.arno.lyramp.feature.authorization.ui.AuthorizationScreen
import com.arno.lyramp.feature.authorization.repository.SpotifyAuthRepository
import com.arno.lyramp.feature.listening_history.ui.ShowListeningHistoryScreen
import org.koin.compose.koinInject

@Composable
fun LyraApp() {
        val authRepo: SpotifyAuthRepository = koinInject()

        val initialScreen = if (!authRepo.getAccessToken().isNullOrEmpty()) {
                ShowListeningHistoryScreen
        } else {
                AuthorizationScreen
        }

        Navigator(initialScreen) { nav ->
                SlideTransition(nav)
        }
}