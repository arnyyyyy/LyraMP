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
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.feature.main.ui.MainScreen
import com.arno.lyramp.feature.onboarding.model.OnboardingStep
import com.arno.lyramp.feature.onboarding.presentation.OnboardingScreenModel
import com.arno.lyramp.feature.onboarding.presentation.OnboardingState
import com.arno.lyramp.feature.onboarding.ui.background.OnboardingBackground
import cafe.adriel.voyager.koin.getScreenModel
import lyramp.composeapp.generated.resources.Res
import lyramp.composeapp.generated.resources.continue_next
import lyramp.composeapp.generated.resources.done
import lyramp.composeapp.generated.resources.lang_chinese
import lyramp.composeapp.generated.resources.lang_english
import lyramp.composeapp.generated.resources.lang_french
import lyramp.composeapp.generated.resources.lang_german
import lyramp.composeapp.generated.resources.lang_hebrew
import lyramp.composeapp.generated.resources.lang_hungarian
import lyramp.composeapp.generated.resources.lang_italian
import lyramp.composeapp.generated.resources.lang_japanese
import lyramp.composeapp.generated.resources.lang_korean
import lyramp.composeapp.generated.resources.lang_portuguese
import lyramp.composeapp.generated.resources.lang_russian
import lyramp.composeapp.generated.resources.lang_spanish
import lyramp.composeapp.generated.resources.error
import lyramp.composeapp.generated.resources.found_languages
import lyramp.composeapp.generated.resources.loading_history
import lyramp.composeapp.generated.resources.loading_init
import lyramp.composeapp.generated.resources.lang_analyze
import lyramp.composeapp.generated.resources.analyzed_res
import lyramp.composeapp.generated.resources.tracks_count
import lyramp.composeapp.generated.resources.retry
import org.jetbrains.compose.resources.stringResource

object OnboardingScreen : Screen {
        @Composable
        override fun Content() {
                val navigator = LocalNavigator.current
                val screenModel = getScreenModel<OnboardingScreenModel>()
                val state by screenModel.state.collectAsState()

                Box(modifier = Modifier.fillMaxSize()) {
                        OnboardingBackground(modifier = Modifier.fillMaxSize())

                        Scaffold(
                                modifier = Modifier.fillMaxSize(),
                                containerColor = Color.Transparent
                        ) { padding ->
                                Column(modifier = Modifier.fillMaxSize().padding(padding)) {
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
                                                                onContinue = { navigator?.replaceAll(MainScreen) }
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
                        .background(LyraColors.GlassCardSurface, RoundedCornerShape(20.dp))
                        .border(1.dp, LyraColors.GlassCardBorder, RoundedCornerShape(20.dp))
                        .padding(28.dp)
        ) {
                Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp), color = LyraColors.Accent)

                        Text(
                                text = when (step) {
                                        OnboardingStep.ENTER_PLAYLIST_URL -> stringResource(Res.string.loading_init)
                                        OnboardingStep.LOADING_HISTORY -> stringResource(Res.string.loading_history)
                                        OnboardingStep.ANALYZING_LANGUAGES -> stringResource(Res.string.lang_analyze)
                                        OnboardingStep.SELECT_LANGUAGES -> stringResource(Res.string.done)
                                },
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = LyraColors.AccentOnAccent
                        )
                }
        }
}

@Composable
private fun SuccessContent(languages: Map<String, Int>, tracksCount: Int, onContinue: () -> Unit) {
        Box(
                modifier = Modifier
                        .widthIn(max = 500.dp)
                        .fillMaxWidth(fraction = 0.9f)
                        .background(LyraColors.GlassCardSurface, RoundedCornerShape(20.dp))
                        .border(1.dp, LyraColors.GlassCardBorder, RoundedCornerShape(20.dp))
                        .padding(28.dp)
        ) {
                Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                        Text(
                                stringResource(Res.string.done),
                                fontSize = 28.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyraColors.OnGlassCard
                        )

                        Text(
                                stringResource(Res.string.analyzed_res, tracksCount),
                                fontSize = 16.sp,
                                color = LyraColors.OnGlassCardSecondary
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                                stringResource(Res.string.found_languages),
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Medium,
                                color = LyraColors.OnGlassCard
                        )

                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                languages.forEach { (lang, count) -> LanguageItem(language = lang, count = count) }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Button(
                                onClick = onContinue,
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = LyraColors.Accent,
                                        contentColor = LyraColors.AccentOnAccent
                                ),
                                shape = RoundedCornerShape(12.dp)
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

@Composable
private fun LanguageItem(language: String, count: Int) {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(LyraColors.CardSurfaceAlt, RoundedCornerShape(8.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
        ) {
                Text(
                        text = getLanguageName(language),
                        fontSize = 16.sp, fontWeight = FontWeight.Medium,
                        color = LyraColors.OnGlassCard
                )
                Text(
                        text = stringResource(
                                Res.string.tracks_count,
                                count
                        ), fontSize = 14.sp,
                        color = LyraColors.OnGlassCardSecondary
                )
        }
}

@Composable
private fun ErrorContent(message: String, onRetry: () -> Unit) {
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
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                        Text(
                                stringResource(Res.string.error),
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyraColors.ErrorText
                        )
                        Text(
                                message, fontSize = 16.sp,
                                color = LyraColors.OnGlassCardSecondary
                        )
                        Button(
                                onClick = onRetry,
                                colors = ButtonDefaults.buttonColors(
                                        containerColor = LyraColors.Accent,
                                        contentColor = LyraColors.AccentOnAccent
                                ),
                                shape = RoundedCornerShape(12.dp)
                        ) {
                                Text(stringResource(Res.string.retry))
                        }
                }
        }
}

@Composable
private fun getLanguageName(code: String): String {
        return when (code.lowercase()) {
                "en" -> stringResource(Res.string.lang_english)
                "ru" -> stringResource(Res.string.lang_russian)
                "es" -> stringResource(Res.string.lang_spanish)
                "fr" -> stringResource(Res.string.lang_french)
                "de" -> stringResource(Res.string.lang_german)
                "it" -> stringResource(Res.string.lang_italian)
                "pt" -> stringResource(Res.string.lang_portuguese)
                "ja" -> stringResource(Res.string.lang_japanese)
                "ko" -> stringResource(Res.string.lang_korean)
                "zh" -> stringResource(Res.string.lang_chinese)
                "hu" -> stringResource(Res.string.lang_hungarian)
                "iw" -> stringResource(Res.string.lang_hebrew)
                else -> "🌍 $code"
        }
}
