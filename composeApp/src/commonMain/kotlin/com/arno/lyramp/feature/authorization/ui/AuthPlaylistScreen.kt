package com.arno.lyramp.feature.authorization.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.repository.AppleAuthRepository
import com.arno.lyramp.feature.onboarding.ui.OnboardingScreen
import com.arno.lyramp.feature.onboarding.ui.StoryProgressBar
import com.arno.lyramp.ui.OnboardingBackground
import com.arno.lyramp.ui.theme.LyraColors
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.auth_playlist_link_label
import lyramp.composeapp.generated.resources.continue_next
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

class AuthPlaylistScreen(val service: MusicServiceType) : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.current
                when (service) {
                        MusicServiceType.APPLE -> {
                                val repo: AppleAuthRepository = koinInject()
                                AuthPlaylistScreenContent(
                                        title = "Apple Music",
                                        initialUrl = repo.getPlaylistUrl() ?: "",
                                        buttonColor = LyraColors.AppleMusic,
                                        onContinue = { url ->
                                                repo.savePlaylistUrl(url.takeIf { it.isNotBlank() })
                                                navigator?.push(OnboardingScreen)
                                        }
                                )
                        }

                        else -> { /* No-op */
                        }
                }
        }
}

@Composable
private fun AuthPlaylistScreenContent(
        title: String,
        initialUrl: String,
        buttonColor: Color,
        onContinue: (String) -> Unit
) {
        var playlistUrl by remember { mutableStateOf(initialUrl) }

        Box(modifier = Modifier.fillMaxSize()) {
                OnboardingBackground(modifier = Modifier.fillMaxSize())

                Scaffold(
                        modifier = Modifier.fillMaxSize(), containerColor = Color.Transparent
                ) { padding ->
                        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                                StoryProgressBar(currentStep = 0, totalSteps = 4)

                                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                        Box(
                                                modifier = Modifier
                                                        .widthIn(max = 400.dp)
                                                        .fillMaxWidth(0.85f)
                                                        .background(LyraColors.GlassCardSurface, RoundedCornerShape(20.dp))
                                                        .border(1.dp, LyraColors.GlassCardBorder, RoundedCornerShape(20.dp))
                                                        .padding(28.dp)
                                        ) {
                                                Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalArrangement = Arrangement.spacedBy(20.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                ) {
                                                        Text(
                                                                title, fontSize = 24.sp, fontWeight = FontWeight.Bold, color = LyraColors.OnGlassCard
                                                        )

                                                        OutlinedTextField(
                                                                value = playlistUrl,
                                                                onValueChange = { playlistUrl = it },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                label = { Text(stringResource(Res.string.auth_playlist_link_label)) },
                                                                colors = OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor = LyraColors.OnGlassCardSecondary,
                                                                        unfocusedBorderColor = LyraColors.OnGlassCardSecondary,
                                                                        focusedLabelColor = LyraColors.OnGlassCardSecondary,
                                                                        unfocusedLabelColor = LyraColors.OnGlassCardSecondary,
                                                                        cursorColor = LyraColors.OnGlassCardSecondary
                                                                ),
                                                                shape = RoundedCornerShape(12.dp),
                                                                singleLine = true
                                                        )

                                                        Button(
                                                                onClick = { onContinue(playlistUrl) },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                colors = ButtonDefaults.buttonColors(
                                                                        containerColor = buttonColor,
                                                                        contentColor = Color.White
                                                                ),
                                                                shape = RoundedCornerShape(12.dp),
                                                                enabled = playlistUrl.isNotBlank()
                                                        ) {
                                                                Text(
                                                                        stringResource(Res.string.continue_next),
                                                                        modifier = Modifier.padding(vertical = 8.dp),
                                                                        fontSize = 16.sp,
                                                                        fontWeight = FontWeight.SemiBold
                                                                )
                                                        }
                                                }
                                        }
                                }
                        }
                }
        }
}
