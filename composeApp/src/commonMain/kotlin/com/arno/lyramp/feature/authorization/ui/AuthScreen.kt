package com.arno.lyramp.feature.authorization.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.AuthNews
import com.arno.lyramp.feature.authorization.presentation.AuthEvent
import com.arno.lyramp.feature.authorization.presentation.AuthState
import com.arno.lyramp.feature.authorization.presentation.AuthorizationScreenModel
import com.arno.lyramp.feature.authorization.presentation.spotify.SpotifyAuthHolder
import com.arno.lyramp.feature.authorization.presentation.spotify.registerSpotifyAuthCallback
import com.arno.lyramp.feature.listening_history.ui.ShowListeningHistoryScreen
import com.arno.lyramp.util.Log
import org.koin.compose.koinInject

object AuthorizationScreen : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.current
                val screenModel: AuthorizationScreenModel = koinInject()

                val state by screenModel.state.collectAsState()

                LaunchedEffect(Unit) {
                        registerSpotifyAuthCallback { code ->
                                SpotifyAuthHolder.callback = null
                                screenModel.onEvent(
                                        AuthEvent.OnAuthCodeReceived(
                                                service = MusicServiceType.SPOTIFY,
                                                code = code
                                        )
                                )
                        }
                }

                LaunchedEffect(Unit) {
                        screenModel.news.collect { effect ->
                                when (effect) {
                                        AuthNews.NavigateToHistory -> {
                                                try {
                                                        navigator?.push(ShowListeningHistoryScreen)
                                                } catch (e: Throwable) {
                                                        Log.logger.e(e) { "AuthorizationScreen: navigation failed" }
                                                }
                                        }
                                }
                        }
                }

                AuthorizationScreenUi(
                        state = state,
                        onSpotifyAuthClick = {
                                screenModel.onEvent(
                                        AuthEvent.OnLoginClick(MusicServiceType.SPOTIFY)
                                )
                        }
                )
        }
}

@Composable
internal fun AuthorizationScreenUi(
        state: AuthState,
        onSpotifyAuthClick: () -> Unit
) {
        Scaffold {
                Column(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                ) {
                        Button(onClick = onSpotifyAuthClick, enabled = !state.isLoading) {
                                Text("Войти через Spotify")
                        }
                        state.error?.let { Text(it) }
                }
        }
}
