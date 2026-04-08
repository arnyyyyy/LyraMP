package com.arno.lyramp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

@Composable
fun OnboardingBackground(
        modifier: Modifier = Modifier
) {

        val notes = remember {
                listOf(
                        MusicNote("♪", -0.7f, -1.2f, 0L, OnboardingColors.Red.copy(alpha = 0.8f)),
                        MusicNote("♫", 0.6f, 1.3f, 800L, OnboardingColors.DeepBlue.copy(alpha = 0.8f)),
                        MusicNote("♪", -0.3f, 2.0f, 1600L, OnboardingColors.Yellow.copy(alpha = 0.8f)),
                        MusicNote("♬", 0.8f, -0.8f, 2400L, OnboardingColors.Blue.copy(alpha = 0.8f)),
                        MusicNote("♩", -0.6f, 1.6f, 3200L, OnboardingColors.AnotherBlue.copy(alpha = 0.8f))
                )
        }

        Box(
                modifier = modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
        ) {
                val backgroundBrush = Brush.verticalGradient(
                        colors = OnboardingColors.BackgroundGradient
                )

                Box(modifier = Modifier.fillMaxSize().background(backgroundBrush))

                notes.forEach { note ->
                        OnboardingAnimatedMusicNote(note = note)
                }
        }
}

private object OnboardingColors {
        val BackgroundGradient = listOf(
                Color(0xFF071021), Color(0xFF13232F), Color(0xFF203544)
        )

        val Red = Color(0xFFFF6B6B)
        val Green = Color(0xFF4ECDC4)
        val Yellow = Color(0xFFFFE66D)
        val Blue = Color(0xFF95E1D3)
        val Purple = Color(0xFFAA96DA)
        val DeepBlue = Color(0xFF6C5CE7)
        val AnotherBlue = Color(0xFF74B9FF)
}

internal data class MusicNote(
        val symbol: String,
        val xPosition: Float,
        val yPosition: Float,
        val delay: Long,
        val color: Color
)

