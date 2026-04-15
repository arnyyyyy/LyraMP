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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Undo
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
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
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.learn_words.data.WordSource
import com.arno.lyramp.feature.learn_words.presentation.LearnWordsUiState
import com.arno.lyramp.feature.learn_words.presentation.WordInfo
import com.arno.lyramp.feature.learn_words.resources.Res
import com.arno.lyramp.feature.learn_words.resources.know_ticked
import com.arno.lyramp.feature.learn_words.resources.learn_ticked
import com.arno.lyramp.ui.PlayAudioButton
import com.arno.lyramp.ui.theme.LyraColors
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import kotlin.math.absoluteValue
import kotlin.math.roundToInt

@Composable
internal fun CardsModeContent(
        state: LearnWordsUiState.Cards,
        onSwipe: (Long, Boolean) -> Unit,
        onUndo: () -> Unit,
        onToggleImportance: (Long, Boolean) -> Unit,
        isLoadingAudio: Boolean,
        onPlayAudio: (WordInfo) -> Unit
) {
        val words = state.words
        val currentIndex = state.currentIndex

        if (currentIndex >= words.size) return

        val currentWord = words[currentIndex]

        val initialOffset = if (state.undoDirection != 0) state.undoDirection * SwipeConfig.OFF_SCREEN_OFFSET else 0f
        val offsetX = remember(currentIndex) { Animatable(initialOffset) }

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

                Spacer(modifier = Modifier.height(8.dp))

                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .height(36.dp),
                        contentAlignment = Alignment.CenterStart
                ) {
                        if (state.canUndo) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Rounded.Undo,
                                        contentDescription = "Undo",
                                        tint = Color.White.copy(alpha = 0.55f),
                                        modifier = Modifier
                                                .size(22.dp)
                                                .clickable(onClick = onUndo)
                                )
                        }
                }

                Box(
                        modifier = Modifier
                                .weight(1f)
                                .widthIn(max = 400.dp)
                                .fillMaxWidth(),
                        contentAlignment = Alignment.Center
                ) {
                        if (currentIndex + 2 < words.size) {
                                Box(modifier = Modifier.fillMaxWidth().fillMaxSize(SwipeConfig.CARD_SIZE_FRACTION)) {
                                        WordCard(
                                                word = words[currentIndex + 2],
                                                isLoadingAudio = false,
                                                onPlayAudio = {},
                                                onToggleImportance = {},
                                                isFront = true,
                                        )
                                }
                        }
                        if (currentIndex + 1 < words.size) {
                                Box(modifier = Modifier.fillMaxWidth().fillMaxSize(SwipeConfig.CARD_SIZE_FRACTION)) {
                                        WordCard(
                                                word = words[currentIndex + 1],
                                                isLoadingAudio = false,
                                                onPlayAudio = {},
                                                onToggleImportance = {},
                                                isFront = true,
                                        )
                                }
                        }

                        key(currentWord.id) {
                                SwipeCard(
                                        word = currentWord,
                                        offsetX = offsetX,
                                        isLoadingAudio = isLoadingAudio,
                                        onPlayAudio = { onPlayAudio(currentWord) },
                                        onToggleImportance = { onToggleImportance(currentWord.id, currentWord.isImportant) },
                                        onSwipe = onSwipe,
                                        enterFromDirection = state.undoDirection,
                                )
                        }
                }

                Spacer(modifier = Modifier.height(16.dp))
        }
}

@Composable
private fun SwipeCard(
        word: WordInfo,
        offsetX: Animatable<Float, *>,
        isLoadingAudio: Boolean,
        onPlayAudio: () -> Unit,
        onToggleImportance: () -> Unit,
        onSwipe: (Long, Boolean) -> Unit,
        enterFromDirection: Int = 0,
) {
        val coroutineScope = rememberCoroutineScope()
        val density = LocalDensity.current.density
        var isFlipped by remember(word.id) { mutableStateOf(false) }

        LaunchedEffect(word.id, enterFromDirection) {
                if (enterFromDirection != 0) offsetX.animateTo(0f, tween(SwipeConfig.UNDO_ANIM_MS))
        }

        val rotation by animateFloatAsState(
                targetValue = if (isFlipped) 180f else 0f,
                animationSpec = tween(durationMillis = 400),
                label = "card_rotation"
        )

        val tiltAngle = (offsetX.value / SwipeConfig.OFF_SCREEN_OFFSET) * SwipeConfig.MAX_TILT_ANGLE

        val swipeAlpha = (offsetX.value.absoluteValue / SwipeConfig.ALPHA_DIVISOR).coerceIn(0f, SwipeConfig.MAX_SWIPE_ALPHA)
        val swipeColor = if (offsetX.value < 0) LyraColors.Correct else LyraColors.Incorrect

        Box(
                modifier = Modifier
                        .fillMaxWidth()
                        .fillMaxSize(SwipeConfig.CARD_SIZE_FRACTION)
                        .graphicsLayer { rotationZ = tiltAngle }
                        .offset { IntOffset(offsetX.value.roundToInt(), 0) }
                        .pointerInput(Unit) {
                                detectDragGestures(
                                        onDragEnd = {
                                                if (offsetX.value.absoluteValue > SwipeConfig.SWIPE_THRESHOLD) {
                                                        coroutineScope.launch {
                                                                val target = if (offsetX.value > 0) SwipeConfig.OFF_SCREEN_OFFSET else -SwipeConfig.OFF_SCREEN_OFFSET
                                                                offsetX.animateTo(targetValue = target, animationSpec = tween(SwipeConfig.SWIPE_ANIM_MS))
                                                                onSwipe(word.id, offsetX.value < 0)
                                                                isFlipped = false
                                                        }
                                                } else {
                                                        coroutineScope.launch { offsetX.animateTo(0f, tween(SwipeConfig.SNAP_BACK_ANIM_MS)) }
                                                }
                                        }
                                ) { change, dragAmount ->
                                        change.consume()
                                        coroutineScope.launch { offsetX.snapTo(offsetX.value + dragAmount.x) }
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
                                isLoadingAudio = isLoadingAudio,
                                onPlayAudio = onPlayAudio,
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
                                modifier = Modifier.fillMaxSize().padding(top = 80.dp),
                                contentAlignment = Alignment.Center
                        ) {
                                Text(
                                        text = stringResource(
                                                if (offsetX.value < 0) Res.string.know_ticked
                                                else Res.string.learn_ticked
                                        ),
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
        isLoadingAudio: Boolean,
        onPlayAudio: () -> Unit,
        onToggleImportance: () -> Unit,
        isFront: Boolean
) {
        val sources = word.sources
        var shownSourceIndex by remember(word.id) { mutableIntStateOf(-1) }

        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .graphicsLayer { rotationY = if (isFront) 0f else 180f }
                        .background(
                                if (isFront) Color.White else LyraColors.CardSurfaceAlt,
                                RoundedCornerShape(20.dp)
                        )
                        .padding(28.dp)
        ) {
                if (isFront) {
                        PlayAudioButton(
                                isLoading = isLoadingAudio,
                                onClick = onPlayAudio,
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
                                                        .background(LyraColors.CardSurfaceAlt, CircleShape)
                                                        .clickable {
                                                                shownSourceIndex = if (shownSourceIndex < sources.size - 1)
                                                                        shownSourceIndex + 1 else -1
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
                                                .background(LyraColors.CardSurfaceAlt, CircleShape)
                                                .clickable { onToggleImportance() },
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
                                        color = LyraColors.TextPrimary,
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
                                        color = LyraColors.TextPrimary,
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
                                color = LyraColors.TextSubtle,
                                textAlign = TextAlign.Center,
                                lineHeight = 16.sp
                        )
                }
                Text(
                        text = "${source.trackName} - ${source.artists}",
                        fontSize = 11.sp,
                        color = LyraColors.TextPlaceholder,
                        textAlign = TextAlign.Center,
                        maxLines = 1
                )
        }
}

private object SwipeConfig {
        const val OFF_SCREEN_OFFSET = 1000f
        const val SWIPE_THRESHOLD = 250f
        const val ALPHA_DIVISOR = 300f
        const val MAX_SWIPE_ALPHA = 0.6f
        const val CARD_SIZE_FRACTION = 0.75f
        const val UNDO_ANIM_MS = 350
        const val SWIPE_ANIM_MS = 200
        const val SNAP_BACK_ANIM_MS = 300
        const val MAX_TILT_ANGLE = 5f
}