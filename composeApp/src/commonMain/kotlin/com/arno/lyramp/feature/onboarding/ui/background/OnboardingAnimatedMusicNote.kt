package com.arno.lyramp.feature.onboarding.ui.background

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive

@Composable
internal fun OnboardingAnimatedMusicNote(note: MusicNote) {
        var currentPhase by remember { mutableStateOf(NotePhase.HIDDEN) }

        val scale by animateFloatAsState(
                targetValue = when (currentPhase) {
                        NotePhase.HIDDEN -> 0.5f
                        NotePhase.APPEARING -> 1.0f
                        NotePhase.VISIBLE -> 1.15f
                        NotePhase.DISAPPEARING -> 1.4f
                },
                animationSpec = tween(
                        durationMillis = when (currentPhase) {
                                NotePhase.HIDDEN -> 0
                                NotePhase.APPEARING -> 2000
                                NotePhase.VISIBLE -> 2600
                                NotePhase.DISAPPEARING -> 1500
                        },
                        easing = FastOutSlowInEasing
                )
        )

        val alpha by animateFloatAsState(
                targetValue = when (currentPhase) {
                        NotePhase.HIDDEN -> 0f
                        NotePhase.APPEARING -> 0.3f
                        NotePhase.VISIBLE -> 0.3f
                        NotePhase.DISAPPEARING -> 0f
                },
                animationSpec = tween(
                        durationMillis = when (currentPhase) {
                                NotePhase.HIDDEN -> 0
                                NotePhase.APPEARING -> 2000
                                NotePhase.VISIBLE -> 2600
                                NotePhase.DISAPPEARING -> 1500
                        }
                )
        )

        LaunchedEffect(Unit) {
                delay(note.delay)

                while (isActive) {
                        currentPhase = NotePhase.APPEARING
                        delay(2000L)

                        currentPhase = NotePhase.VISIBLE
                        delay(2600L)

                        currentPhase = NotePhase.DISAPPEARING
                        delay(1500L)

                        currentPhase = NotePhase.HIDDEN
                        delay(800L)
                }
        }

        Box(
                modifier = Modifier
                        .fillMaxSize()
                        .offset(
                                x = (note.xPosition * 120).dp,
                                y = (note.yPosition * 180).dp
                        ),
                contentAlignment = Alignment.Center
        ) {
                Text(
                        text = note.symbol,
                        fontSize = 64.sp,
                        fontWeight = FontWeight.Light,
                        color = note.color,
                        modifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                        }
                )
        }
}

private enum class NotePhase {
        HIDDEN,
        APPEARING,
        VISIBLE,
        DISAPPEARING
}

