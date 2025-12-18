package com.arno.lyramp.feature.onboarding.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
internal fun StoryProgressBar(
        currentStep: Int,
        totalSteps: Int,
        modifier: Modifier = Modifier
) {
        Row(
                modifier = modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
                repeat(totalSteps) { index ->
                        val progress = when {
                                index < currentStep -> 1f
                                index == currentStep -> 0.5f
                                else -> 0f
                        }

                        val animatedProgress by animateFloatAsState(targetValue = progress)

                        Box(
                                modifier = Modifier
                                        .weight(1f)
                                        .height(3.dp)
                                        .clip(RoundedCornerShape(2.dp))
                                        .background(Color.White.copy(alpha = 0.3f))
                        ) {
                                Box(
                                        modifier = Modifier
                                                .fillMaxWidth(animatedProgress)
                                                .height(3.dp)
                                                .background(Color.White)
                                )
                        }
                }
        }
}

