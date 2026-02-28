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
import cafe.adriel.voyager.navigator.Navigator
import com.arno.lyramp.feature.authorization.model.MusicServiceType
import com.arno.lyramp.feature.authorization.repository.AuthPlaylistRepository
import com.arno.lyramp.feature.authorization.repository.AppleAuthRepository
import com.arno.lyramp.feature.onboarding.ui.OnboardingScreen
import com.arno.lyramp.feature.onboarding.ui.StoryProgressBar
import com.arno.lyramp.feature.onboarding.ui.background.OnboardingBackground
import lyramp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource
import org.koin.compose.koinInject

class AuthPlaylistScreen(val service: MusicServiceType) : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.current
                when (service) {
                        MusicServiceType.APPLE -> {
                                val repo: AppleAuthRepository = koinInject()
                                AuthPlaylistScreenContent(service = service, repo = repo, navigator = navigator)
                        }

                        else -> { /* No-op */
                        }
                }
        }
}

@Composable
fun AuthPlaylistScreenContent(
        service: MusicServiceType,
        repo: AuthPlaylistRepository,
        navigator: Navigator?
) {
        val title = when (service) {
                MusicServiceType.APPLE -> "Apple Music"
                else -> ""
        }

        val cardBgColor = Color.White
        val titleColor = Color.Black
        val focusedBorderColor = Color.Gray
        val unfocusedBorderColor = Color.Gray
        val buttonContainerColor = Color(0xFFFF0436)


        var playlistUrl by remember { mutableStateOf(repo.getPlaylistUrl() ?: "") }

        Box(modifier = Modifier.fillMaxSize()) {
                OnboardingBackground(modifier = Modifier.fillMaxSize())

                Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent
                ) { padding ->
                        Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                                StoryProgressBar(
                                        currentStep = 0,
                                        totalSteps = 4
                                )

                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) {
                                        Box(
                                                modifier = Modifier
                                                        .widthIn(max = 400.dp)
                                                        .fillMaxWidth(0.85f)
                                                        .background(
                                                                color = cardBgColor,
                                                                shape = RoundedCornerShape(20.dp)
                                                        )
                                                        .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                                                        .padding(horizontal = 28.dp, vertical = 32.dp)
                                        ) {
                                                Column(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalArrangement = Arrangement.spacedBy(20.dp),
                                                        horizontalAlignment = Alignment.CenterHorizontally,
                                                ) {
                                                        Text(
                                                                title,
                                                                fontSize = 24.sp,
                                                                fontWeight = FontWeight.Bold,
                                                                color = titleColor
                                                        )

                                                        OutlinedTextField(
                                                                value = playlistUrl,
                                                                onValueChange = { playlistUrl = it },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                label = { Text(stringResource(Res.string.auth_playlist_link_label)) },
                                                                colors = OutlinedTextFieldDefaults.colors(
                                                                        focusedBorderColor = focusedBorderColor,
                                                                        unfocusedBorderColor = unfocusedBorderColor,
                                                                        focusedLabelColor = focusedBorderColor,
                                                                        unfocusedLabelColor = unfocusedBorderColor,
                                                                        cursorColor = focusedBorderColor
                                                                ),
                                                                shape = RoundedCornerShape(12.dp),
                                                                singleLine = true
                                                        )

                                                        Button(
                                                                onClick = {
                                                                        repo.savePlaylistUrl(playlistUrl.takeIf { it.isNotBlank() })
                                                                        navigator?.push(OnboardingScreen)
                                                                },
                                                                modifier = Modifier.fillMaxWidth(),
                                                                colors = ButtonDefaults.buttonColors(
                                                                        containerColor = buttonContainerColor,
                                                                        contentColor = Color.White
                                                                ),
                                                                shape = RoundedCornerShape(12.dp),
                                                                enabled = playlistUrl.isNotBlank()
                                                        ) {
                                                                Text(
                                                                        stringResource(Res.string.auth_continue),
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
