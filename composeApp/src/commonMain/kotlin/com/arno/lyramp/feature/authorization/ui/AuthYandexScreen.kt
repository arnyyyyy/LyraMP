package com.arno.lyramp.feature.authorization.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.arno.lyramp.feature.authorization.repository.AuthPlaylistRepository
import com.arno.lyramp.feature.listening_history.ui.ShowListeningHistoryScreen
import org.koin.compose.koinInject

object AuthYandexScreen : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.current
                val repo: AuthPlaylistRepository = koinInject()

                var playlistUrl by remember { mutableStateOf(repo.getPlaylistUrl() ?: "") }

                Scaffold {
                        Column(
                                modifier = Modifier.fillMaxSize(),
                                verticalArrangement = Arrangement.Center,
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                OutlinedTextField(
                                        value = playlistUrl,
                                        onValueChange = { playlistUrl = it },
                                        modifier = Modifier.fillMaxWidth(0.9f),
                                        label = { Text("Ссылка на плейлист Яндекс") }
                                )

                                Button(onClick = {
                                        repo.savePlaylistUrl(playlistUrl.takeIf { it.isNotBlank() })
                                        navigator?.push(ShowListeningHistoryScreen)
                                }) {
                                        Text("Продолжить")
                                }
                        }
                }
        }
}
