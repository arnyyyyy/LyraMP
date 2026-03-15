package com.arno.lyramp.feature.learn_words.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.learn_words.data.WordSource
import com.arno.lyramp.feature.learn_words.presentation.WordInfo
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import com.arno.lyramp.feature.translation.speech.TranslationSpeechController
import kotlinx.coroutines.launch
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
internal fun CardsModeContent(
        state: LearnWordsUiState.Cards,
        onSwipe: (Long, Boolean) -> Unit,
        onToggleImportance: (Long, Boolean) -> Unit,
        onRequestSpeech: suspend (WordInfo) -> String?
) {
        val words = state.words
        val currentIndex = state.currentIndex

        if (currentIndex >= words.size) return

        val currentWord = words[currentIndex]

        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .widthIn(max = 600.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                ProgressHeader(
                        currentIndex = state.currentIndex,
                        totalCount = state.totalCount,
                        correctCount = state.correctCount,
                        incorrectCount = state.incorrectCount
                )

                Spacer(modifier = Modifier.height(24.dp))

                Box(
                        modifier = Modifier
                                .weight(1f)
                                .widthIn(max = 400.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                ) {
                        SwipeCard(
                                word = currentWord,
                                onRequestSpeech = onRequestSpeech,
                                onToggleImportance = { onToggleImportance(currentWord.id, currentWord.isImportant) },
                                onSwipe = onSwipe,
                        )
                }

                Spacer(modifier = Modifier.height(16.dp))
        }
}


@Composable
private fun SwipeCard(
        word: WordInfo,
        onRequestSpeech: suspend (WordInfo) -> String?,
        onToggleImportance: () -> Unit,
        onSwipe: (Long, Boolean) -> Unit,
) {
        val offsetX = remember { Animatable(0f) }
        val coroutineScope = rememberCoroutineScope()
        val density = LocalDensity.current.density
        var isFlipped by remember(word.id) { mutableStateOf(false) }

        val rotation by animateFloatAsState(
                targetValue = if (isFlipped) 180f else 0f,
                animationSpec = tween(durationMillis = 400),
                label = "card_flip"
        )

        val maxTiltAngle = 5f
        val maxOffset = 1000f
        val tiltAngle = (offsetX.value / maxOffset) * maxTiltAngle

        val swipeAlpha = (offsetX.value.absoluteValue / 300f).coerceIn(0f, 0.6f)
        val swipeColor = if (offsetX.value < 0) Color(0xFF34C759) else Color(0xFFFF3B30)

        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer {
                                rotationZ = tiltAngle
                        }
                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                        .pointerInput(word.id) {
                                detectDragGestures(
                                        onDragEnd = {
                                                val threshold = 250f
                                                if (offsetX.value.absoluteValue > threshold) {
                                                        coroutineScope.launch {
                                                                val target = if (offsetX.value > 0) 1000f else -1000f
                                                                offsetX.animateTo(
                                                                        targetValue = target,
                                                                        animationSpec = tween(200)
                                                                )
                                                                onSwipe(word.id, offsetX.value < 0)

                                                                isFlipped = false
                                                                offsetX.snapTo(0f)
                                                        }
                                                } else {
                                                        coroutineScope.launch {
                                                                offsetX.animateTo(0f, tween(300))
                                                        }
                                                }
                                        }
                                ) { change, dragAmount ->
                                        change.consume()
                                        coroutineScope.launch {
                                                offsetX.snapTo(offsetX.value + dragAmount.x)
                                        }
                                }
                        }
                        .clickable { isFlipped = !isFlipped }
        ) {
                Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .graphicsLayer {
                                        rotationY = rotation
                                        cameraDistance = 12 * density
                                },
                        contentAlignment = Alignment.Center
                ) {
                        WordCard(
                                word = word,
                                onRequestSpeech = onRequestSpeech,
                                onToggleImportance = onToggleImportance,
                                isFront = rotation <= 90f
                        )
                }

                if (swipeAlpha > 0.05f) {
                        Box(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .alpha(swipeAlpha)
                                        .background(swipeColor.copy(alpha = 0.15f), RoundedCornerShape(20.dp))
                        )
                        Box(
                                modifier = Modifier.fillMaxSize(),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = if (offsetX.value < 0) "✓ Знаю" else "📖 Учить",
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = swipeColor.copy(alpha = swipeAlpha * 1.5f)
                                )
                        }
                }
        }
}

@Composable
private fun WordCard(
        word: WordInfo,
        onRequestSpeech: suspend (WordInfo) -> String?,
        onToggleImportance: () -> Unit,
        isFront: Boolean
) {
        val sources = word.sources
        var shownSourceIndex by remember(word.id) { mutableIntStateOf(-1) }

        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(0.75f)
                        .graphicsLayer { rotationY = if (isFront) 0f else 180f }
                        .background(
                                if (isFront) Color.White else Color(0xFFF2F2F7),
                                RoundedCornerShape(20.dp)
                        )
                        .padding(28.dp)
        ) {

                if (isFront) {
                        PlayAudioButton(
                                word = word,
                                onRequestSpeech = onRequestSpeech,
                                modifier = Modifier.align(Alignment.TopEnd)
                        )

                        Row(
                                modifier = Modifier.align(Alignment.TopStart),
                                horizontalArrangement = Arrangement.spacedBy(6.dp),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                if (sources.isNotEmpty()) {
                                        Box(
                                                modifier = Modifier
                                                        .size(36.dp)
                                                        .background(Color(0xFFF2F2F7), CircleShape)
                                                        .clickable {
                                                                shownSourceIndex = if (shownSourceIndex < sources.size - 1) {
                                                                        shownSourceIndex + 1
                                                                } else {
                                                                        -1
                                                                }
                                                        },
                                                contentAlignment = Alignment.Center
                                        ) {
                                                Text(
                                                        text = if (shownSourceIndex >= 0) "💡" else "🔅",
                                                        fontSize = 16.sp
                                                )
                                        }
                                }

                                Box(
                                        modifier = Modifier
                                                .size(36.dp)
                                                .background(Color(0xFFF2F2F7), CircleShape)
                                                .clickable {
                                                        onToggleImportance()
                                                },
                                        contentAlignment = Alignment.Center
                                ) {
                                        Text(
                                                text = if (word.isImportant) "⭐" else "☆",
                                                fontSize = 18.sp
                                        )
                                }
                        }
                }

                Column(
                        modifier = Modifier.fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                ) {
                        if (isFront) {
                                Text(
                                        text = word.word,
                                        fontSize = 30.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1C1C1E),
                                        textAlign = TextAlign.Center
                                )

                                Spacer(Modifier.height(16.dp))

                                AnimatedVisibility(
                                        visible = shownSourceIndex >= 0 && shownSourceIndex < sources.size,
                                        enter = fadeIn() + expandVertically(),
                                        exit = fadeOut() + shrinkVertically()
                                ) {
                                        if (shownSourceIndex >= 0 && shownSourceIndex < sources.size) {
                                                SourceHint(source = sources[shownSourceIndex])
                                        }
                                }
                        } else {
                                Text(
                                        text = word.translation,
                                        fontSize = 26.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1C1C1E),
                                        textAlign = TextAlign.Center
                                )
                        }
                }
        }
}

@Composable
private fun SourceHint(source: WordSource) {
        Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
                if (source.lyricLine.isNotBlank()) {
                        Text(
                                text = "\"${source.lyricLine}\"",
                                fontSize = 12.sp,
                                color = Color(0xFFAEAEB2),
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                        )
                }
                Text(
                        text = "${source.trackName} - ${source.artists}",
                        fontSize = 11.sp,
                        color = Color(0xFFC7C7CC),
                        textAlign = TextAlign.Center,
                        maxLines = 1
                )
        }
}

@Composable
private fun PlayAudioButton(
        word: WordInfo,
        onRequestSpeech: suspend (WordInfo) -> String?,
        modifier: Modifier = Modifier
) {
        val speechController = remember { TranslationSpeechController() }
        val coroutineScope = rememberCoroutineScope()
        var isLoadingAudio by remember { mutableStateOf(false) }

        Box(
                modifier = modifier
                        .size(36.dp)
                        .background(Color(0xFFF2F2F7), CircleShape)
                        .clickable(enabled = !isLoadingAudio) {
                                isLoadingAudio = true

                                coroutineScope.launch {
                                        val filePath = onRequestSpeech(word)

                                        if (filePath != null) {
                                                speechController.play(filePath)
                                        }

                                        isLoadingAudio = false
                                }
                        },
                contentAlignment = Alignment.Center
        ) {
                if (isLoadingAudio) {
                        CircularProgressIndicator(
                                modifier = Modifier.size(16.dp),
                                strokeWidth = 1.5.dp,
                                color = Color(0xFF8E8E93)
                        )
                } else {
                        Text("🔊", fontSize = 16.sp)
                }
        }
}
