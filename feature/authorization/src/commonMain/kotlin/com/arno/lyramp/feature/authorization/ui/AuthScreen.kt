package com.arno.lyramp.feature.authorization.ui

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.arno.lyramp.feature.authorization.domain.model.MusicServiceType
import com.arno.lyramp.feature.authorization.presentation.AuthEvent
import com.arno.lyramp.feature.authorization.presentation.AuthNews
import com.arno.lyramp.feature.authorization.presentation.AuthorizationScreenModel
import com.arno.lyramp.feature.authorization.presentation.launchAuthUrl
import com.arno.lyramp.feature.authorization.presentation.yandex.YandexAuthBus
import com.arno.lyramp.feature.authorization.ui.background.AuthBackground
import com.arno.lyramp.core.navigation.ScreenFactory
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.painterResource
import cafe.adriel.voyager.koin.getScreenModel
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.authorization.resources.Res
import com.arno.lyramp.feature.authorization.resources.auth_continue_without_auth
import com.arno.lyramp.feature.authorization.resources.auth_login_with_yandex
import com.arno.lyramp.feature.authorization.resources.auth_select_service
import com.arno.lyramp.feature.authorization.resources.yandex_icon
import org.koin.compose.koinInject

object AuthScreen : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.current
                val screenModel = getScreenModel<AuthorizationScreenModel>()
                val screenFactory: ScreenFactory = koinInject()
                val yandexAuthBus: YandexAuthBus = koinInject()

                val state by screenModel.state.collectAsState()

                LaunchedEffect(Unit) {
                        yandexAuthBus.flow.collect { result ->
                                screenModel.onEvent(
                                        AuthEvent.OnYandexAuthCompleted(
                                                token = result.token,
                                                expiresIn = result.expiresIn
                                        )
                                )
                                yandexAuthBus.consume()
                        }
                }

                LaunchedEffect(Unit) {
                        screenModel.news.collect { effect ->
                                when (effect) {
                                        AuthNews.NavigateToOnboarding ->
                                                navigator?.push(screenFactory.onboardingScreen())

                                        AuthNews.NavigateToOptionalPlaylistInput ->
                                                navigator?.push(AuthPlaylistScreen(MusicServiceType.NONE))

                                        is AuthNews.LaunchAuth ->
                                                launchAuthUrl(effect.url, effect.service)
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
                                                                Color.White,
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

                                                        Button(
                                                                onClick = { onAuthClick(MusicServiceType.YANDEX) },
                                                                enabled = !state.isLoading,
                                                                modifier = Modifier.height(52.dp),
                                                                shape = RoundedCornerShape(18.dp),
                                                                colors = ButtonDefaults.buttonColors(
                                                                        containerColor = Color.White,
                                                                        contentColor = Color.DarkGray
                                                                ),
                                                                border = BorderStroke(width = 4.dp, color = LyraColors.Yandex),
                                                                contentPadding = PaddingValues(horizontal = 16.dp),
                                                        ) {
                                                                Row(
                                                                        verticalAlignment = Alignment.CenterVertically,
                                                                ) {
                                                                        Image(
                                                                                painter = painterResource(Res.drawable.yandex_icon),
                                                                                contentDescription = null,
                                                                                modifier = Modifier.size(24.dp),
                                                                        )
                                                                        Spacer(modifier = Modifier.width(8.dp))
                                                                        Text(
                                                                                stringResource(Res.string.auth_login_with_yandex),
                                                                                fontSize = 18.sp,
                                                                        )
                                                                }
                                                        }

                                                        Text(
                                                                stringResource(Res.string.auth_continue_without_auth),
                                                                fontSize = 16.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = LyraColors.OnGlassCardSecondary,
                                                                modifier = Modifier
                                                                        .padding(top = 18.dp)
                                                                        .clickable {
                                                                                onAuthClick(MusicServiceType.NONE)
                                                                        },
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
}
