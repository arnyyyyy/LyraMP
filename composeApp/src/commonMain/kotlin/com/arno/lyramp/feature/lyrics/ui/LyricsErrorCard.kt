package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lyramp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ShowLyricsErrorCard(error: String, onRetry: () -> Unit) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.White, RoundedCornerShape(16.dp))
                        .border(1.dp, Color(0xFFE8E8E8), RoundedCornerShape(16.dp))
                        .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Text(
                        text = "🎵",
                        fontSize = 56.sp
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                        text = stringResource(Res.string.lyrics_not_found),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color(0xFF2C3E50)
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                        text = error,
                        fontSize = 14.sp,
                        color = Color(0xFF7F8C8D),
                        textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(24.dp))
                Text(
                        text = stringResource(Res.string.lyrics_retry),
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White,
                        modifier = Modifier
                                .background(Color(0xFF4A90E2), RoundedCornerShape(12.dp))
                                .clickable { onRetry() }
                                .padding(horizontal = 32.dp, vertical = 12.dp)
                )
        }
}