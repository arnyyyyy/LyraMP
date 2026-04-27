package com.arno.lyramp.feature.stats.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.core.model.CefrDifficultyGroup
import com.arno.lyramp.feature.stats.domain.model.CefrGroupStats
import com.arno.lyramp.feature.stats.domain.model.LanguageStatsSnapshot
import com.arno.lyramp.feature.stats.resources.Res
import com.arno.lyramp.feature.stats.resources.stats_card_learned
import com.arno.lyramp.feature.stats.resources.stats_card_learning
import com.arno.lyramp.feature.stats.resources.stats_empty_cefr
import com.arno.lyramp.feature.stats.resources.stats_library_fully_learned
import com.arno.lyramp.feature.stats.resources.stats_library_processed
import com.arno.lyramp.feature.stats.resources.stats_library_total
import com.arno.lyramp.feature.stats.resources.stats_section_cefr
import com.arno.lyramp.feature.stats.resources.stats_section_library
import com.arno.lyramp.feature.stats.resources.stats_section_vocabulary
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun StatsContent(snapshot: LanguageStatsSnapshot) {
        val scrollState = rememberScrollState()
        Column(
                modifier = Modifier.fillMaxSize()
                        .verticalScroll(scrollState)
                        .padding(horizontal = 20.dp, vertical = 12.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
        ) {
                VocabularySection(snapshot = snapshot)
                CefrProgressSection(groupStats = snapshot.groupStats)
                LibrarySection(snapshot = snapshot)
                Spacer(modifier = Modifier.height(24.dp))
        }
}

@Composable
private fun SectionHeader(text: String) {
        Text(
                text = text,
                color = Color.White.copy(alpha = 0.55f),
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                letterSpacing = 1.6.sp,
        )
}

@Composable
private fun VocabularySection(snapshot: LanguageStatsSnapshot) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(stringResource(Res.string.stats_section_vocabulary))
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                        NumberCard(
                                modifier = Modifier.weight(1f),
                                value = snapshot.learningWordsCount,
                                label = stringResource(Res.string.stats_card_learning),
                                accent = Color(0xFF4A90E2),
                        )
                        NumberCard(
                                modifier = Modifier.weight(1f),
                                value = snapshot.learnedWordsCount,
                                label = stringResource(Res.string.stats_card_learned),
                                accent = Color(0xFF34C759),
                        )
                }
        }
}

@Composable
private fun NumberCard(
        modifier: Modifier,
        value: Int,
        label: String,
        accent: Color,
) {
        Column(
                modifier = modifier
                        .background(Color.White.copy(alpha = 0.10f), RoundedCornerShape(16.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.16f), RoundedCornerShape(16.dp))
                        .padding(horizontal = 16.dp, vertical = 18.dp),
                verticalArrangement = Arrangement.spacedBy(6.dp),
        ) {
                Text(
                        text = value.toString(),
                        color = accent,
                        fontSize = 36.sp,
                        fontWeight = FontWeight.Bold,
                )
                Text(
                        text = label,
                        color = Color.White.copy(alpha = 0.75f),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium,
                )
        }
}

@Composable
private fun CefrProgressSection(groupStats: List<CefrGroupStats>) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(stringResource(Res.string.stats_section_cefr))

                val hasAnyData = groupStats.any { it.total > 0 }
                Column(
                        modifier = Modifier.fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(14.dp),
                ) {
                        if (!hasAnyData) {
                                Text(
                                        text = stringResource(Res.string.stats_empty_cefr),
                                        color = Color.White.copy(alpha = 0.75f),
                                        fontSize = 14.sp,
                                )
                        } else {
                                groupStats.forEach { CefrGroupRow(it) }
                        }
                }
        }
}

@Composable
private fun CefrGroupRow(stat: CefrGroupStats) {
        val color = when (stat.group) {
                CefrDifficultyGroup.BEGINNER -> Color(0xFF4CAF50)
                CefrDifficultyGroup.INTERMEDIATE -> Color(0xFFFF9800)
                CefrDifficultyGroup.ADVANCED -> Color(0xFFF44336)
        }
        val percentage = (stat.ratio * 100).toInt()
        Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                        Text(text = stat.group.emoji, fontSize = 16.sp)
                        Text(
                                text = stat.group.label,
                                color = Color.White,
                                fontSize = 15.sp,
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.weight(1f),
                        )
                        Text(
                                text = if (stat.total == 0) "—" else "$percentage%  (${stat.known} / ${stat.total})",
                                color = Color.White.copy(alpha = 0.80f),
                                fontSize = 13.sp,
                        )
                }
                LinearProgressIndicator(
                        progress = { stat.ratio.coerceIn(0f, 1f) },
                        modifier = Modifier.fillMaxWidth().height(8.dp).clip(RoundedCornerShape(4.dp)),
                        color = color,
                        trackColor = Color.White.copy(alpha = 0.12f),
                )
        }
}

@Composable
private fun LibrarySection(snapshot: LanguageStatsSnapshot) {
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                SectionHeader(stringResource(Res.string.stats_section_library))
                Column(
                        modifier = Modifier.fillMaxWidth()
                                .background(Color.White.copy(alpha = 0.08f), RoundedCornerShape(16.dp))
                                .border(1.dp, Color.White.copy(alpha = 0.14f), RoundedCornerShape(16.dp))
                                .padding(horizontal = 16.dp, vertical = 14.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                ) {
                        LibraryRow(
                                emoji = "🎵",
                                label = stringResource(Res.string.stats_library_total),
                                value = snapshot.libraryTracksCount.toString(),
                        )
                        LibraryRow(
                                emoji = "✅",
                                label = stringResource(Res.string.stats_library_fully_learned),
                                value = snapshot.fullyLearnedTracksCount.toString(),
                        )
                        LibraryRow(
                                emoji = "🔄",
                                label = stringResource(Res.string.stats_library_processed),
                                value = "${snapshot.processedTracksCount} / ${snapshot.libraryTracksCount.coerceAtLeast(snapshot.processedTracksCount)}",
                        )
                }
        }
}

@Composable
private fun LibraryRow(emoji: String, label: String, value: String) {
        Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
        ) {
                Text(text = emoji, fontSize = 18.sp)
                Text(
                        text = label,
                        color = Color.White,
                        fontSize = 15.sp,
                        modifier = Modifier.weight(1f),
                )
                Text(
                        text = value,
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.SemiBold,
                )
        }
}
