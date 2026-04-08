package com.arno.lyramp.feature.authorization.ui

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.AuthEvent
import com.arno.lyramp.feature.authorization.presentation.AuthNews
import com.arno.lyramp.feature.authorization.presentation.AuthorizationScreenModel
import com.arno.lyramp.feature.authorization.presentation.launchAuthUrl
import com.arno.lyramp.feature.authorization.presentation.spotify.SpotifyAuthHolder
import com.arno.lyramp.feature.authorization.presentation.yandex.YandexAuthHolder
import com.arno.lyramp.feature.authorization.ui.background.AuthBackground
import com.arno.lyramp.feature.onboarding.ui.OnboardingScreen
import com.arno.lyramp.util.Log
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import cafe.adriel.voyager.koin.getScreenModel
import com.arno.lyramp.ui.theme.LyraColors
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.apple_icon
import lyramp.composeapp.generated.resources.auth_continue_without_auth
import lyramp.composeapp.generated.resources.auth_select_service
import lyramp.composeapp.generated.resources.spotify_icon
import lyramp.composeapp.generated.resources.yandex_icon

object AuthScreen : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.current
                val screenModel = getScreenModel<AuthorizationScreenModel>()

                val state by screenModel.state.collectAsState()

                // TODO: подумать, как можно вынести
                LaunchedEffect(Unit) {
                        SpotifyAuthHolder.authCodeFlow.collect { code ->
                                screenModel.onEvent(
                                        AuthEvent.OnAuthCodeReceived(
                                                service = MusicServiceType.SPOTIFY,
                                                code = code
                                        )
                                )
                        }
                }

                LaunchedEffect(Unit) {
                        YandexAuthHolder.authResultFlow.collect { result ->
                                screenModel.onEvent(
                                        AuthEvent.OnAuthCodeReceived(
                                                service = MusicServiceType.YANDEX,
                                                code = "${result.token}_token_expiresIn_${result.expiresIn}"
                                        )
                                )
                        }
                }

                LaunchedEffect(Unit) {
                        screenModel.news.collect { effect ->
                                when (effect) {
                                        AuthNews.NavigateToOnboarding -> {
                                                try {
                                                        navigator?.push(OnboardingScreen)
                                                } catch (e: Throwable) {
                                                        Log.logger.e(e) { "$TAG: navigation to Onboarding screen" }
                                                }
                                        }

                                        AuthNews.NavigateToAppleEnterPlaylist -> {
                                                try {
                                                        navigator?.push(AuthPlaylistScreen(MusicServiceType.APPLE))
                                                } catch (e: Throwable) {
                                                        Log.logger.e(e) { "$TAG: navigation to Apple screen" }
                                                }
                                        }

                                        is AuthNews.LaunchAuth -> {
                                                try {
                                                        launchAuthUrl(effect.url, effect.service)
                                                } catch (e: Throwable) {
                                                        Log.logger.e(e) { "$TAG: launch auth failed for ${effect.service}" }
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
                                        modifier = Modifier.fillMaxSize().padding(innerPadding),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Box(
                                                modifier = Modifier
                                                        .widthIn(max = 400.dp)
                                                        .fillMaxWidth(0.85f)
                                                        .background(
                                                                LyraColors.Accent,
                                                                RoundedCornerShape(20.dp)
                                                        )
                                                        .border(
                                                                1.dp, LyraColors.GlassCardBorder,
                                                                RoundedCornerShape(20.dp)
                                                        )
                                                        .padding(horizontal = 28.dp, vertical = 32.dp)
                                        ) {
                                                Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalArrangement = Arrangement.Center,
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                ) {
                                                        Text(
                                                                stringResource(Res.string.auth_select_service),
                                                                fontSize = 24.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = LyraColors.AccentOnAccent,
                                                                modifier = Modifier.padding(bottom = 24.dp)
                                                        )

                                                        Row(
                                                                verticalAlignment = Alignment.CenterVertically,
                                                                horizontalArrangement = Arrangement.Center
                                                        ) {
                                                                authTypes.forEachIndexed { _, (service, icon, color) ->
                                                                        Button(
                                                                                onClick = { onAuthClick(service) },
                                                                                enabled = !state.isLoading,
                                                                                modifier = Modifier
                                                                                        .padding(horizontal = 12.dp)
                                                                                        .size(48.dp)
                                                                                        .clip(RoundedCornerShape(24.dp)),
                                                                                shape = RoundedCornerShape(24.dp),
                                                                                colors = ButtonDefaults.outlinedButtonColors(containerColor = color),
                                                                                contentPadding = PaddingValues(0.dp),
                                                                        ) {
                                                                                Image(
                                                                                        painter = painterResource(icon),
                                                                                        contentDescription = service.name,
                                                                                        modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(24.dp)),
                                                                                        contentScale = ContentScale.Fit
                                                                                )
                                                                        }
                                                                }
                                                        }

                                                        Text(
                                                                stringResource(Res.string.auth_continue_without_auth),
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = LyraColors.OnGlassCardSecondary,
                                                                modifier = Modifier.padding(top = 14.dp),
                                                                style = TextStyle(textDecoration = TextDecoration.Underline)
                                                        )
                                                }
                                                state.error?.let {
                                                        Text(
                                                                it,
                                                                modifier = Modifier.padding(top = 16.dp),
                                                                color = LyraColors.ErrorText.copy(alpha = 0.7f),
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
                        LyraColors.Spotify.copy(alpha = 0.12f)
                ),
                Triple(
                        MusicServiceType.YANDEX, Res.drawable.yandex_icon,
                        LyraColors.Yandex.copy(alpha = 0.12f)
                ),
                Triple(
                        MusicServiceType.APPLE, Res.drawable.apple_icon,
                        LyraColors.OnGlassCardSecondary.copy(alpha = 0.08f)
                )
        )

        private const val TAG = "AuthorizationScreen"
}
