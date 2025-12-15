package com.arno.lyramp.feature.authorization.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import cafe.adriel.voyager.core.screen.Screen
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.navigator.LocalNavigator
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.AuthNews
import com.arno.lyramp.feature.authorization.presentation.AuthEvent
import com.arno.lyramp.feature.authorization.presentation.AuthorizationScreenModel
import com.arno.lyramp.feature.authorization.presentation.spotify.SpotifyAuthHolder
import com.arno.lyramp.feature.authorization.presentation.spotify.registerSpotifyAuthCallback
import com.arno.lyramp.feature.authorization.ui.background.AuthBackground
import com.arno.lyramp.feature.listening_history.ui.ShowListeningHistoryScreen
import com.arno.lyramp.util.Log
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.apple_icon
import lyramp.composeapp.generated.resources.spotify_icon
import lyramp.composeapp.generated.resources.yandex_icon
import org.jetbrains.compose.resources.painterResource
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

                                        AuthNews.NavigateToYandexEnterPlaylist -> {
                                                try {
                                                        navigator?.push(AuthYandexScreen)
                                                } catch (e: Throwable) {
                                                        Log.logger.e(e) { "AuthorizationScreen: navigation to Yandex screen failed" }
                                                }
                                        }
                                }
                        }
                }

                val onAuthClick = { service: MusicServiceType ->
                        screenModel.onEvent(
                                AuthEvent.OnLoginClick(service)
                        )
                }

                Box(modifier = Modifier.fillMaxSize()) {
                        AuthBackground(modifier = Modifier.fillMaxSize())

                        Scaffold(modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent) { innerPadding ->
                                Box(
                                        modifier = Modifier
                                                .fillMaxSize()
                                                .padding(innerPadding),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Box(
                                                modifier = Modifier
                                                        .widthIn(max = 400.dp)
                                                        .fillMaxWidth(0.85f)
                                                        .background(
                                                                color = Color(0xFFFFCC00),
                                                                shape = RoundedCornerShape(20.dp)
                                                        )
                                                        .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                                        .padding(horizontal = 28.dp, vertical = 32.dp)
                                        ) {
                                                Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                ) {
                                                        Text(
                                                                "Выберите сервис",
                                                                fontSize = 24.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.DarkGray,
                                                                modifier = Modifier.padding(bottom = 24.dp)
                                                        )

                                                        Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.Center
                                                        ) {
                                                                authTypes.forEachIndexed { idx, (service, icon, color) ->
                                                                        Button(
                                                                                onClick = { onAuthClick(service) },
                                                                                enabled = !state.isLoading,
                                                                                modifier = Modifier
                                                                                        .padding(horizontal = 12.dp)
                                                                                        .size(48.dp)
                                                                                        .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp)),
                                                                                shape = androidx.compose.foundation.shape.RoundedCornerShape(24.dp),
                                                                                colors = ButtonDefaults.outlinedButtonColors(containerColor = color),
                                                                                contentPadding = androidx.compose.foundation.layout.PaddingValues(0.dp),
                                                                        ) {
                                                                                Image(
                                                                                        painter = painterResource(icon),
                                                                                        contentDescription = service.name,
                                                                                        modifier = Modifier
                                                                                                .fillMaxSize()
                                                                                                .clip(androidx.compose.foundation.shape.RoundedCornerShape(24.dp)),
                                                                                        contentScale = ContentScale.Fit
                                                                                )
                                                                        }
                                                                }
                                                        }

                                                        Text(
                                                                "Продолжить без авторизации",
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = Color.Gray.copy(alpha = 0.8f),
                                                                modifier = Modifier.padding(top = 14.dp),
                                                                style = TextStyle(textDecoration = TextDecoration.Underline)

                                                        )
                                                }
                                                state.error?.let {
                                                        Text(
                                                                it,
                                                                modifier = Modifier.padding(top = 16.dp),
                                                                color = Color.Red.copy(alpha = 0.7f),
                                                                fontSize = 12.sp
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }

        val authTypes = listOf(
                Triple(
                        MusicServiceType.SPOTIFY, Res.drawable.spotify_icon,
                        Color(0xFF1DB954).copy(alpha = 0.12f)
                ),
                Triple(
                        MusicServiceType.YANDEX, Res.drawable.yandex_icon,
                        Color(0xFFFFCC00).copy(alpha = 0.12f)
                ),
                Triple(
                        MusicServiceType.APPLE, Res.drawable.apple_icon,
                        Color.Gray.copy(alpha = 0.08f)
                )
        )
}
