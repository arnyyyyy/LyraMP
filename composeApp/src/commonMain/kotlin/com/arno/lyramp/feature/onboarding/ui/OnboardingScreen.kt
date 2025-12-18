package com.arno.lyramp.feature.onboarding.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import com.arno.lyramp.feature.listening_history.ui.ShowListeningHistoryScreen
import com.arno.lyramp.feature.onboarding.model.OnboardingStep
import com.arno.lyramp.feature.onboarding.presentation.OnboardingScreenModel
import com.arno.lyramp.feature.onboarding.presentation.OnboardingState
import com.arno.lyramp.feature.onboarding.ui.background.OnboardingBackground
import org.koin.compose.koinInject

object OnboardingScreen : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.current
                val screenModel: OnboardingScreenModel = koinInject()
                val state by screenModel.state.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())

                        Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                containerColor = Color.Transparent
                        ) { padding ->
                                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
                                        // Progress bar
                                        StoryProgressBar(
                                                currentStep = when (state) {
                                                        is OnboardingState.Loading -> {
                                                                when ((state as OnboardingState.Loading).step) {
                                                                        OnboardingStep.ENTER_PLAYLIST_URL -> 0
                                                                        OnboardingStep.LOADING_HISTORY -> 1
                                                                        OnboardingStep.ANALYZING_LANGUAGES -> 2
                                                                        OnboardingStep.SELECT_LANGUAGES -> 3
                                                                }
                                                        }

                                                        is OnboardingState.Success -> 3
                                                        is OnboardingState.Error -> 1
                                                },
                                                totalSteps = 4
                                        )

                                        Box(
                                                modifier = Modifier.fillMaxSize(),
                                                contentAlignment = Alignment.Center
                                        ) {
                                                when (val currentState = state) {
                                                        is OnboardingState.Loading -> LoadingContent(currentState.step)
                                                        is OnboardingState.Success -> SuccessContent(
                                                                languages = currentState.languages,
                                                                tracksCount = currentState.tracks.size,
                                                                onContinue = { navigator?.push(ShowListeningHistoryScreen) }
                                                        )

                                                        is OnboardingState.Error -> ErrorContent(
                                                                message = currentState.message,
                                                                onRetry = { screenModel.retry() }
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
private fun LoadingContent(step: OnboardingStep) {
        Box(
                modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(0.85f)
                        .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(40.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                        CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = Color(0xFFFFCC00)
                        )

                        Text(
                                text = when (step) {
                                        OnboardingStep.ENTER_PLAYLIST_URL -> "Инициализация..."
                                        OnboardingStep.LOADING_HISTORY -> "Загружаем музыку..."
                                        OnboardingStep.ANALYZING_LANGUAGES -> "Анализируем языки..."
                                        OnboardingStep.SELECT_LANGUAGES -> "Готово!"
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                        )
                }
        }
}

@Composable
private fun SuccessContent(
        languages: Map<String, Int>,
        tracksCount: Int,
        onContinue: () -> Unit
) {
        Box(
                modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth(0.9f)
                        .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(28.dp)
        ) {
                Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                        Text(
                                "Готово!",
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                        )

                        Text(
                                "Проанализировано $tracksCount треков",
                                fontSize = 16.sp,
                                color = Color.Gray
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                                "Найденные языки:",
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.DarkGray
                        )

                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                languages.forEach { (lang, count) ->
                                        LanguageItem(language = lang, count = count)
                                }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                                onClick = onContinue,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFCC00),
                                        contentColor = Color.DarkGray
                                ),
                                shape = RoundedCornerShape(12.dp)
                        ) {
                                Text(
                                        "Продолжить",
                                        modifier = Modifier.padding(vertical = 8.dp),
                                        fontSize = 16.sp,
                                        fontWeight = FontWeight.SemiBold
                                )
                        }
                }
        }
}

@Composable
private fun LanguageItem(language: String, count: Int) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFF5F5F5), RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                        text = getLanguageName(language),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                )

                Text(
                        text = "$count треков",
                        fontSize = 14.sp,
                        color = Color.Gray
                )
        }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
        Box(
                modifier = Modifier
                        .widthIn(max = 400.dp)
                        .fillMaxWidth(0.85f)
                        .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(20.dp))
                        .border(1.dp, Color.LightGray.copy(alpha = 0.2f), RoundedCornerShape(20.dp))
                        .padding(28.dp)
        ) {
                Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        Text(
                                "Ошибка",
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Red
                        )

                        Text(
                                message,
                                fontSize = 16.sp,
                                color = Color.Gray
                        )

                        Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = Color(0xFFFFCC00),
                                        contentColor = Color.DarkGray
                                ),
                                shape = RoundedCornerShape(12.dp)
                        ) {
                                Text("Попробовать снова")
                        }
                }
        }
}

private fun getLanguageName(code: String): String {
        return when (code.lowercase()) {
                "en" -> "🇬🇧 Английский"
                "ru" -> "🇷🇺 Русский"
                "es" -> "🇪🇸 Испанский"
                "fr" -> "🇫🇷 Французский"
                "de" -> "🇩🇪 Немецкий"
                "it" -> "🇮🇹 Итальянский"
                "pt" -> "🇵🇹 Португальский"
                "ja" -> "🇯🇵 Японский"
                "ko" -> "🇰🇷 Корейский"
                "zh" -> "🇨🇳 Китайский"
                "hu" -> "🇭🇺 Венгерский"
                "iw" -> "🇮🇱 Иврит"
                else -> "🌍 $code"
        }
}
