package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
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
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.OutlinedButton
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
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.listening_practice.model.LineCheckResult
import com.arno.lyramp.feature.listening_practice.presentation.AuditionScreenModel
import com.arno.lyramp.feature.listening_practice.presentation.AuditionUiState
import com.arno.lyramp.feature.listening_practice.resources.Res
import com.arno.lyramp.feature.listening_practice.resources.accuracy
import com.arno.lyramp.feature.listening_practice.resources.audition_completed
import com.arno.lyramp.feature.listening_practice.resources.audition_empty
import com.arno.lyramp.feature.listening_practice.resources.audition_loading
import com.arno.lyramp.feature.listening_practice.resources.audition_play_again
import com.arno.lyramp.feature.listening_practice.resources.audition_round_progress
import com.arno.lyramp.feature.listening_practice.resources.audition_subtitle
import com.arno.lyramp.feature.listening_practice.resources.audition_title
import com.arno.lyramp.feature.listening_practice.resources.back
import com.arno.lyramp.feature.listening_practice.resources.correct
import com.arno.lyramp.feature.listening_practice.resources.incorrect
import com.arno.lyramp.feature.listening_practice.resources.result
import com.arno.lyramp.ui.EmptyStateCard
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.MainFeatureScaffold
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource
import org.koin.core.parameter.parametersOf

data class AuditionScreen(val language: String?) : Screen {

        @Composable
        override fun Content() {
                val navigator = LocalNavigator.currentOrThrow
                val screenModel = getScreenModel<AuditionScreenModel> { parametersOf(language) }
                val uiState by screenModel.uiState.collectAsState()
                val readyState = uiState as? AuditionUiState.Ready


                MainFeatureScaffold(
                        icon = "🎙️",
                        title = stringResource(Res.string.audition_title),
                        subtitle = stringResource(Res.string.audition_subtitle),
                        onBack = { navigator.pop() },
                        actions = {
                                readyState?.let { state ->
                                        CompactPracticeProgress(
                                                correctCount = state.correctCount,
                                                incorrectCount = state.incorrectCount,
                                                title = "${state.roundIndex + 1}/${state.roundSize}",
                                        )
                                }
                        },
                ) {
                        when (val state = uiState) {
                                is AuditionUiState.Loading -> LoadingCard(
                                        message = stringResource(Res.string.audition_loading),
                                )

                                is AuditionUiState.Empty -> EmptyStateCard(
                                        icon = "🎧",
                                        title = stringResource(Res.string.audition_empty),
                                        subtitle = "",
                                )

                                is AuditionUiState.Error -> ErrorCard(message = state.message)
                                is AuditionUiState.Ready -> AuditionReadyContent(
                                        state = state,
                                        onPlayCurrentLine = screenModel::onPlayCurrentLineClick,
                                        onUserInputChange = screenModel::onUserInputChange,
                                        onCheck = screenModel::onCheckLine,
                                        onSkip = screenModel::onSkipLine,
                                        onNext = screenModel::onNextLine,
                                        onToggleSlowMode = screenModel::onToggleSlowMode,
                                        onExpandStart = screenModel::onExpandStart,
                                        onExpandEnd = screenModel::onExpandEnd,
                                )

                                is AuditionUiState.Completed -> AuditionCompletedContent(
                                        state = state,
                                        onPlayAgain = screenModel::onPlayAgain,
                                        onBack = { navigator.pop() },
                                )
                        }
                }
        }
}

@Composable
private fun AuditionReadyContent(
        state: AuditionUiState.Ready,
        onPlayCurrentLine: () -> Unit,
        onUserInputChange: (String) -> Unit,
        onCheck: () -> Unit,
        onSkip: () -> Unit,
        onNext: () -> Unit,
        onToggleSlowMode: () -> Unit,
        onExpandStart: () -> Unit,
        onExpandEnd: () -> Unit,
) {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
                AnimatedContent(
                        targetState = state.currentLine to state.track,
                        transitionSpec = {
                                (fadeIn(tween(300))
                                    + slideInVertically(tween(350)) { it / 8 }) togetherWith
                                    (fadeOut(tween(200))
                                        + slideOutVertically(tween(250)) { -it / 8 })
                        },
                        label = "audition_card",
                ) { (_, track) ->
                        LinePlayCard(
                                track = track,
                                isPlaying = state.currentLineIsPlaying,
                                isPlayerReady = state.isPlayerReady,
                                isSlowMode = state.isSlowMode,
                                onPlayCurrentLine = onPlayCurrentLine,
                                onToggleSlowMode = onToggleSlowMode,
                                onExpandStart = onExpandStart,
                                onExpandEnd = onExpandEnd,
                                modifier = Modifier.fillMaxWidth(),
                        )
                }

                Spacer(modifier = Modifier.weight(1f))

                AnimatedVisibility(
                        visible = state.lastAnsweredLine != null,
                        enter = fadeIn(tween(250))
                            + slideInVertically(tween(300)) { it },
                        exit = fadeOut(tween(150)),
                ) {
                        state.lastAnsweredLine?.let { AnswerReviewCard(line = it) }
                }

                Spacer(modifier = Modifier.height(8.dp))

                PracticeInputBlock(
                        userInput = state.userInput,
                        onUserInputChange = onUserInputChange,
                        onCheck = onCheck,
                        onSkip = onSkip,
                        onTransparentSurface = true,
                        isAnswered = state.lastAnsweredLine != null,
                        onNext = onNext,
                )

                Spacer(modifier = Modifier.height(8.dp))
        }
}

@Composable
private fun AuditionCompletedContent(
        state: AuditionUiState.Completed,
        onPlayAgain: () -> Unit,
        onBack: () -> Unit,
) {
        val scrollState = rememberScrollState()
        val total = state.correctCount + state.incorrectCount
        val percentage = if (total > 0) state.correctCount * 100 / total else 0

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(24.dp),
        ) {
                // Заголовок с результатом
                Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                        Text(text = if (percentage >= 80) "🏆" else if (percentage >= 50) "🎯" else "💪", fontSize = 52.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                                text = stringResource(Res.string.audition_completed),
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyraColorScheme.onSurface,
                        )
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(horizontalArrangement = Arrangement.spacedBy(20.dp)) {
                                AuditionStatCard(
                                        label = stringResource(Res.string.correct),
                                        value = state.correctCount,
                                        color = LyraColors.Success,
                                )
                                AuditionStatCard(
                                        label = stringResource(Res.string.incorrect),
                                        value = state.incorrectCount,
                                        color = LyraColors.Incorrect,
                                )
                        }

                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                                text = stringResource(Res.string.accuracy, percentage),
                                fontSize = 18.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColorScheme.primary,
                        )
                }

                Spacer(modifier = Modifier.height(20.dp))

                Column(
                        modifier = Modifier
                                .weight(1f)
                                .verticalScroll(scrollState),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                        Text(
                                text = stringResource(Res.string.result),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColorScheme.onSurface,
                        )
                        state.answeredLines.forEach { line ->
                                if (line.checkResult != LineCheckResult.PENDING) {
                                        AnswerReviewCard(line = line)
                                }
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                        OutlinedButton(
                                onClick = onBack,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                        ) { Text(stringResource(Res.string.back)) }

                        Button(
                                onClick = onPlayAgain,
                                modifier = Modifier.weight(1f).height(50.dp),
                                shape = RoundedCornerShape(14.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = LyraColorScheme.primary),
                        ) {
                                Text(
                                        stringResource(Res.string.audition_play_again),
                                        fontWeight = FontWeight.SemiBold,
                                )
                        }
                }
        }
}

@Composable
private fun AuditionStatCard(label: String, value: Int, color: Color) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                        .background(color.copy(alpha = 0.1f), RoundedCornerShape(12.dp))
                        .padding(horizontal = 22.dp, vertical = 12.dp),
        ) {
                Text(text = value.toString(), fontSize = 30.sp, fontWeight = FontWeight.Bold, color = color)
                Text(text = label, fontSize = 13.sp, color = LyraColorScheme.onSurfaceVariant)
        }
}
