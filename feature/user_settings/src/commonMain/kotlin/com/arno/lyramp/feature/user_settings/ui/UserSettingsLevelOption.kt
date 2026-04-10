package com.arno.lyramp.feature.user_settings.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.user_settings.model.RecommendedWordLevel
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.util.getLanguageFlag

@Composable
internal fun LanguageChip(
        code: String,
        isSelected: Boolean,
        onClick: () -> Unit,
) {
        val bgColor = if (isSelected) LyraColorScheme.primary.copy(alpha = 0.12f) else LyraColorScheme.surfaceVariant
        val borderColor = if (isSelected) LyraColorScheme.primary else LyraColorScheme.outline
        val textColor = if (isSelected) LyraColorScheme.primary else LyraColorScheme.onSurface

        Row(
                modifier = Modifier
                        .background(bgColor, RoundedCornerShape(12.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable(onClick = onClick)
                        .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
                Text(text = getLanguageFlag(code), fontSize = 18.sp)
                Text(
                        text = getLanguageDisplayName(code),
                        fontSize = 14.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = textColor,
                )
        }
}

@Composable
internal fun LanguageLevelCard(
        language: String,
        currentLevel: RecommendedWordLevel,
        onSelectLevel: (RecommendedWordLevel) -> Unit,
) {
        var expanded by remember { mutableStateOf(false) }

        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(LyraColorScheme.surfaceVariant, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
        ) {
                Row(
                        modifier = Modifier
                                .fillMaxWidth()
                                .clickable { expanded = !expanded }
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                ) {
                        Text(text = getLanguageFlag(language), fontSize = 22.sp)
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                                text = getLanguageDisplayName(language),
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColorScheme.onSurface,
                                modifier = Modifier.weight(1f)
                        )
                        Text(
                                text = "${currentLevel.emoji} ${currentLevel.label}",
                                fontSize = 14.sp,
                                color = LyraColorScheme.primary,
                                fontWeight = FontWeight.Medium
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                                text = if (expanded) "▲" else "▼",
                                fontSize = 12.sp,
                                color = LyraColorScheme.onSurfaceVariant
                        )
                }

                AnimatedVisibility(
                        visible = expanded,
                        enter = expandVertically(),
                        exit = shrinkVertically()
                ) {
                        Column(
                                modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(start = 16.dp, end = 16.dp, bottom = 12.dp),
                                verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                                RecommendedWordLevel.entries.forEach { level ->
                                        WordLevelOption(
                                                level = level,
                                                isSelected = level == currentLevel,
                                                onClick = {
                                                        onSelectLevel(level)
                                                        expanded = false
                                                }
                                        )
                                }
                        }
                }
        }
}

@Composable
private fun WordLevelOption(
        level: RecommendedWordLevel,
        isSelected: Boolean,
        onClick: () -> Unit,
) {
        val bgColor = if (isSelected) LyraColorScheme.primary.copy(alpha = 0.10f) else Color.Transparent
        val borderColor = if (isSelected) LyraColorScheme.primary else LyraColorScheme.outline

        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(bgColor, RoundedCornerShape(12.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(12.dp))
                        .clickable(onClick = onClick)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
        ) {
                Text(text = level.emoji, fontSize = 18.sp)
                Spacer(modifier = Modifier.width(10.dp))
                Text(
                        text = level.label.padEnd(10),
                        fontSize = 15.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) LyraColorScheme.primary else LyraColorScheme.onSurface,
                        modifier = Modifier.weight(1f)
                )
                if (isSelected) {
                        Text(text = "✓", fontSize = 16.sp, color = LyraColorScheme.primary, fontWeight = FontWeight.Bold)
                }
        }
}

private fun getLanguageDisplayName(code: String): String = when (code) {
        "en" -> "English"
        "fr" -> "Français"
        "de" -> "Deutsch"
        "es" -> "Español"
        "it" -> "Italiano"
        "hu" -> "Magyar"
        "ja" -> "日本語"
        "zh" -> "中文"
        "he" -> "עברית"
        "ar" -> "العربية"
        else -> "${getLanguageFlag(code)} $code"
}