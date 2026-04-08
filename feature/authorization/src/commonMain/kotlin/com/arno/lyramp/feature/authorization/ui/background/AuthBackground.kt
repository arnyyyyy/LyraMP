package com.arno.lyramp.feature.authorization.ui.background

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@Composable
internal fun AuthBackground(
        modifier: Modifier = Modifier
) {
        val greetings = remember {
                listOf(
                        GreetingItem(
                                "Привет", AuthBackgroundColors.Red,
                                -0.65f, -1.15f, 0L
                        ),
                        GreetingItem(
                                "Hello", AuthBackgroundColors.Green,
                                0.45f, 1.15f, 800L
                        ),
                        GreetingItem(
                                "Bonjour", AuthBackgroundColors.Yellow,
                                -0.25f, 2.1f, 1600L
                        ),
                        GreetingItem(
                                "Hola", AuthBackgroundColors.Blue,
                                0.75f, -0.75f, 2400L
                        ),
                        GreetingItem(
                                "こんにちは", AuthBackgroundColors.Purple,
                                -0.65f, 1.45f, 3200L
                        ),
                        GreetingItem(
                                "你好", AuthBackgroundColors.DeepBlue,
                                0.75f, -1.8f, 4000L
                        ),
                )
        }

        val backgroundBrush = Brush.verticalGradient(
                colors = AuthBackgroundColors.BackgroundGradient
        )

        Box(
                modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center
        ) {
                Box(modifier = Modifier.fillMaxSize().background(backgroundBrush))
                AnimatedCircles()

                greetings.forEach { greeting ->
                        Greeting(greeting = greeting)
                }
        }
}


internal data class GreetingItem(
        val text: String, val color: Color, val offsetX: Float, val offsetY: Float, val startDelay: Long
)

internal object AuthBackgroundColors {
        val Red = Color(0xFFFF6B6B)
        val Green = Color(0xFF4ECDC4)
        val Yellow = Color(0xFFFFE66D)
        val Blue = Color(0xFF95E1D3)
        val Purple = Color(0xFFAA96DA)
        val DeepBlue = Color(0xFF6C5CE7)
        val AnotherBlue = Color(0xFF74B9FF)

        val BackgroundGradient = listOf(
                Color(0xFF071021), Color(0xFF13232F), Color(0xFF203544)
        )
//        val BackgroundGradient = listOf(
//                Color(0xFFFFFBF0), Color(0xFFFFF7E6),
//                Color(0xFFFFF3CC)
//        )
}
