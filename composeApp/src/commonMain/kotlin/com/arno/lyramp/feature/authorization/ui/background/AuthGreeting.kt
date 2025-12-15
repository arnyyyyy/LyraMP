package com.arno.lyramp.feature.authorization.ui.background

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
internal fun Greeting(greeting: GreetingItem) {
        var currentPhase by remember { mutableStateOf(GreetingPhase.HIDDEN) }

        val scale by animateFloatAsState(
                targetValue = when (currentPhase) {
                        GreetingPhase.HIDDEN -> 0.5f
                        GreetingPhase.APPEARING -> 1.0f
                        GreetingPhase.VISIBLE -> 1.15f
                        GreetingPhase.DISAPPEARING -> 1.4f
                },
                animationSpec = tween(
                        durationMillis = when (currentPhase) {
                                GreetingPhase.HIDDEN -> 0
                                GreetingPhase.APPEARING -> AnimationTimings.APPEARING_DELAY
                                GreetingPhase.VISIBLE -> AnimationTimings.VISIBLE_DELAY
                                GreetingPhase.DISAPPEARING -> AnimationTimings.DISAPPEARING_DELAY
                        },
                        easing = FastOutSlowInEasing
                ),
        )

        val alpha by animateFloatAsState(
                targetValue = when (currentPhase) {
                        GreetingPhase.HIDDEN -> 0f
                        GreetingPhase.APPEARING -> 1f
                        GreetingPhase.VISIBLE -> 1f
                        GreetingPhase.DISAPPEARING -> 0f
                },
                animationSpec = tween(
                        durationMillis = when (currentPhase) {
                                GreetingPhase.HIDDEN -> 0
                                GreetingPhase.APPEARING -> AnimationTimings.APPEARING_DELAY
                                GreetingPhase.VISIBLE -> AnimationTimings.VISIBLE_DELAY
                                GreetingPhase.DISAPPEARING -> AnimationTimings.DISAPPEARING_DELAY
                        }
                ),
        )

        LaunchedEffect(Unit) {
                delay(greeting.startDelay)

                while (isActive) {
                        currentPhase = GreetingPhase.APPEARING
                        delay(AnimationTimings.APPEARING_DELAY.toLong())

                        currentPhase = GreetingPhase.VISIBLE
                        delay(AnimationTimings.VISIBLE_DELAY.toLong())

                        currentPhase = GreetingPhase.DISAPPEARING
                        delay(AnimationTimings.DISAPPEARING_DELAY.toLong())

                        currentPhase = GreetingPhase.HIDDEN
                        delay(AnimationTimings.HIDDEN_DELAY.toLong())
                }
        }

        Box(
                modifier = Modifier.fillMaxSize().offset(
                        x = (greeting.offsetX * 120).dp,
                        y = (greeting.offsetY * 180).dp
                ),
                contentAlignment = Alignment.Center
        ) {
                Text(
                        text = greeting.text,
                        fontSize = 42.sp,
                        fontWeight = FontWeight.Bold,
                        color = greeting.color.copy(alpha = 0.85f),
                        modifier = Modifier.graphicsLayer {
                                scaleX = scale
                                scaleY = scale
                                this.alpha = alpha
                        }
                )
        }
}

private object AnimationTimings {
        const val APPEARING_DELAY = 2000
        const val VISIBLE_DELAY = 2600
        const val DISAPPEARING_DELAY = 1500
        const val HIDDEN_DELAY = 800
}


private enum class GreetingPhase {
        HIDDEN,
        APPEARING,
        VISIBLE,
        DISAPPEARING
}
