package com.arno.lyramp.feature.stories_generator.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.stories_generator.model.DownloadableModel
import com.arno.lyramp.feature.stories_generator.model.ModelDownloadState
import com.arno.lyramp.feature.stories_generator.resources.Res
import com.arno.lyramp.feature.stories_generator.resources.downloading_proc
import com.arno.lyramp.feature.stories_generator.resources.model_change
import com.arno.lyramp.feature.stories_generator.resources.model_default_name
import com.arno.lyramp.feature.stories_generator.resources.model_loading
import com.arno.lyramp.feature.stories_generator.resources.model_ready
import com.arno.lyramp.feature.stories_generator.resources.nums_description
import com.arno.lyramp.feature.stories_generator.resources.percent
import com.arno.lyramp.feature.stories_generator.resources.retry
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ModelDownloadCard(
        modelState: ModelDownloadState,
        activeModel: DownloadableModel?,
        onDownload: (DownloadableModel) -> Unit,
        onDelete: () -> Unit
) {
        when (modelState) {
                is ModelDownloadState.NotDownloaded -> {
                        Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                                DownloadableModel.entries.forEach { model ->
                                        Box(
                                                modifier = Modifier
                                                        .fillMaxWidth()
                                                        .background(LyraColorScheme.surface, RoundedCornerShape(14.dp))
                                                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(14.dp))
                                                        .padding(14.dp)
                                        ) {
                                                Row(
                                                        modifier = Modifier.fillMaxWidth(),
                                                        verticalAlignment = Alignment.CenterVertically,
                                                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                                                ) {
                                                        Text(text = "🧠", fontSize = 28.sp)

                                                        Column(modifier = Modifier.weight(1f)) {
                                                                Text(
                                                                        text = model.label,
                                                                        fontSize = 15.sp,
                                                                        fontWeight = FontWeight.SemiBold,
                                                                        color = LyraColorScheme.onSurface
                                                                )
                                                                Text(
                                                                        text = stringResource(Res.string.nums_description, model.description, model.sizeLabel),
                                                                        fontSize = 12.sp,
                                                                        color = LyraColorScheme.onSurfaceVariant
                                                                )
                                                        }

                                                        Button(
                                                                onClick = { onDownload(model) },
                                                                shape = RoundedCornerShape(10.dp),
                                                                colors = ButtonDefaults.buttonColors(
                                                                        containerColor = LyraColorScheme.primary
                                                                )
                                                        ) {
                                                                Text(text = "⬇", fontSize = 14.sp, color = Color.White)
                                                        }
                                                }
                                        }
                                }
                        }
                }

                is ModelDownloadState.Downloading -> {
                        val animatedProgress by animateFloatAsState(
                                targetValue = modelState.progress,
                                label = "download_progress"
                        )
                        val percent = (animatedProgress * 100).toInt()

                        SurfaceCard {
                                Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalAlignment = Alignment.CenterHorizontally
                                ) {
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                                Text(
                                                        text = stringResource(
                                                                Res.string.downloading_proc,
                                                                activeModel?.label ?: stringResource(Res.string.model_default_name)
                                                        ),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Medium,
                                                        color = LyraColorScheme.onSurface
                                                )
                                                Text(
                                                        text = stringResource(Res.string.percent, percent),
                                                        fontSize = 14.sp,
                                                        fontWeight = FontWeight.Bold,
                                                        color = LyraColorScheme.primary
                                                )
                                        }
                                        Spacer(modifier = Modifier.height(8.dp))
                                        LinearProgressIndicator(
                                                progress = { animatedProgress },
                                                modifier = Modifier
                                                        .fillMaxWidth()
                                                        .height(6.dp)
                                                        .clip(RoundedCornerShape(3.dp)),
                                                color = LyraColorScheme.primary,
                                                trackColor = LyraColorScheme.outline
                                        )
                                }
                        }
                }

                is ModelDownloadState.Checking -> {
                        SurfaceCard {
                                Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.Center
                                ) {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                color = LyraColorScheme.primary,
                                                strokeWidth = 2.dp
                                        )
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                                text = stringResource(Res.string.model_loading),
                                                fontSize = 14.sp,
                                                color = LyraColorScheme.onSurfaceVariant
                                        )
                                }
                        }
                }

                is ModelDownloadState.Downloaded -> {
                        if (activeModel != null) {
                                SurfaceCard {
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                                Column(modifier = Modifier.weight(1f)) {
                                                        Text(
                                                                text = activeModel.label,
                                                                fontSize = 14.sp,
                                                                color = LyraColorScheme.onSurface
                                                        )
                                                        Text(
                                                                text = stringResource(Res.string.model_ready, activeModel.sizeLabel),
                                                                fontSize = 10.sp,
                                                                color = LyraColorScheme.onSurfaceVariant
                                                        )
                                                }

                                                Button(
                                                        onClick = onDelete,
                                                        shape = RoundedCornerShape(10.dp),
                                                        colors = ButtonDefaults.buttonColors(
                                                                containerColor = LyraColors.Incorrect.copy(alpha = 0.15f)
                                                        )
                                                ) {
                                                        Text(text = "🗑", fontSize = 14.sp)
                                                }
                                        }
                                }
                        }
                }

                is ModelDownloadState.Error -> {
                        SurfaceCard {
                                Column(
                                        modifier = Modifier.fillMaxWidth(),
                                        verticalArrangement = Arrangement.spacedBy(12.dp)
                                ) {
                                        Row(
                                                verticalAlignment = Alignment.CenterVertically,
                                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                                        ) {
                                                Text(text = "⚠️", fontSize = 24.sp)
                                                Text(
                                                        text = modelState.message,
                                                        fontSize = 13.sp,
                                                        color = LyraColorScheme.error
                                                )
                                        }
                                        Row(
                                                modifier = Modifier.fillMaxWidth(),
                                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                                        ) {
                                                if (activeModel != null) {
                                                        StoryButton(
                                                                text = stringResource(Res.string.retry),
                                                                onClick = { onDownload(activeModel) },
                                                                modifier = Modifier.weight(1f)
                                                        )
                                                }
                                                StoryButton(
                                                        text = stringResource(Res.string.model_change),
                                                        onClick = onDelete,
                                                        modifier = Modifier.weight(1f),
                                                        containerColor = LyraColors.Incorrect.copy(alpha = 0.6f)
                                                )
                                        }
                                }
                        }
                }
        }
}


@Composable
private fun SurfaceCard(
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
) {
        Box(
                modifier = modifier
                        .fillMaxWidth()
                        .background(LyraColorScheme.surface, RoundedCornerShape(14.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(14.dp))
                        .padding(16.dp)
        ) {
                content()
        }
}