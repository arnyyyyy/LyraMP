package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
internal fun ShowLyricsErrorCard(error: String) {
        Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                )
        )
        {
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                ) {
                        Text(
                                text = "⚠️",
                                style = MaterialTheme.typography.headlineSmall
                        )
                        Spacer(modifier = Modifier.width(12.dp))
                        Text(
                                text = error,
                                style = MaterialTheme.typography.bodyLarge.copy(
                                        color = MaterialTheme.colorScheme.onErrorContainer,
                                        fontWeight = FontWeight.Medium
                                )
                        )
                }
        }
        Spacer(modifier = Modifier.height(24.dp))
}