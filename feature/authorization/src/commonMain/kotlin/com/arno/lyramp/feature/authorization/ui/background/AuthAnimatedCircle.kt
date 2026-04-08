package com.arno.lyramp.feature.authorization.ui.background

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun AnimatedCircle(
        modifier: Modifier = Modifier, size: Dp,
        translationXPx: () -> Float, translationYPy: () -> Float,
        alpha: Float, color: Color, shape: Shape,
        offsetX: Dp = 0.dp, offsetY: Dp = 0.dp,
) {
        Box(
                modifier = modifier
                        .size(size)
                        .layout { measurable, constraints ->
                                val placeable = measurable.measure(constraints)

                                val offsetXPx = offsetX.roundToPx()
                                val offsetYPx = offsetY.roundToPx()

                                val tx = (translationXPx() + offsetXPx).roundToInt()
                                val ty = (translationYPy() + offsetYPx).roundToInt()

                                layout(placeable.width, placeable.height) {
                                        placeable.place(tx, ty)
                                }
                        }
                        .graphicsLayer {
                                this.alpha = alpha
                        }
                        .background(color, shape = shape)
        )
}

@Composable
internal fun BoxScope.AnimatedCircles() {
        val circles = remember {
                listOf(
                        CircleParams(
                                size = 220.dp, translationX = 1.0f, translationY = -0.6f, alpha = 0.15f,
                                alignment = Alignment.TopStart, offsetX = (-60).dp, offsetY = (-60).dp,
                                color = AuthBackgroundColors.Red, shape = CircleShape,
                        ),
                        CircleParams(
                                size = 180.dp, translationX = 0.7f, translationY = 0.5f, alpha = 0.10f,
                                alignment = Alignment.Center, offsetX = 100.dp, offsetY = (-80).dp,
                                color = AuthBackgroundColors.AnotherBlue,
                                shape = RoundedCornerShape(
                                        topStart = 0.dp,
                                        topEnd = 0.dp,
                                        bottomStart = 0.dp,
                                        bottomEnd = 150.dp,
                                ),
                        ),
                        CircleParams(
                                size = 160.dp, translationX = -1.0f, translationY = 0.8f, alpha = 0.12f,
                                alignment = Alignment.BottomEnd, offsetX = 60.dp, offsetY = 60.dp,
                                color = AuthBackgroundColors.Yellow, shape = CircleShape,
                        ),
                )
        }

        val transition = rememberInfiniteTransition()
        val animX = transition.animateFloat(
                initialValue = -40f,
                targetValue = 40f,
                animationSpec = infiniteRepeatable(
                        tween(durationMillis = 5000, easing = LinearEasing),
                        RepeatMode.Reverse
                ),
        )
        val animY = transition.animateFloat(
                initialValue = -30f,
                targetValue = 30f,
                animationSpec = infiniteRepeatable(
                        tween(durationMillis = 6500, easing = LinearEasing),
                        RepeatMode.Reverse
                ),
        )

        circles.forEach { c ->
                AnimatedCircle(
                        modifier = Modifier.align(c.alignment), size = c.size,
                        translationXPx = { c.translationX * animX.value }, translationYPy = { c.translationY * animY.value },
                        alpha = c.alpha, color = c.color, shape = c.shape, offsetX = c.offsetX, offsetY = c.offsetY,
                )
        }
}

private data class CircleParams(
        val size: Dp, val translationX: Float, val translationY: Float, val alpha: Float,
        val alignment: Alignment, val offsetX: Dp, val offsetY: Dp, val color: Color, val shape: Shape,
)
