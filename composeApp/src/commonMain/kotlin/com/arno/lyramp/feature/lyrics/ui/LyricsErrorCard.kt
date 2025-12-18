package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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

@Composable
internal fun ShowLyricsErrorCard(error: String) {
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
                        text = "Текст не найден",
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
        }
}