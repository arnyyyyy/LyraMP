package com.arno.lyramp.feature.listening_history.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.listeningHistory.resources.Res
import com.arno.lyramp.feature.listeningHistory.resources.folders
import com.arno.lyramp.feature.listening_history.presentation.FolderItem
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun FolderFilterSheet(
        folderItems: List<FolderItem>,
        selectedSourceId: String?,
        onSelect: (String?) -> Unit,
        onDismiss: () -> Unit,
) {
        val sheetState = rememberModalBottomSheetState()

        ModalBottomSheet(
                onDismissRequest = onDismiss,
                sheetState = sheetState,
                containerColor = LyraColorScheme.surface,
        ) {
                Column(
                        modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 24.dp)
                                .padding(bottom = 32.dp)
                                .navigationBarsPadding(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                        Text(
                                text = stringResource(Res.string.folders),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = LyraColorScheme.onSurface,
                                modifier = Modifier.padding(bottom = 12.dp),
                        )

                        folderItems.forEach { folder ->
                                FolderRow(
                                        item = folder,
                                        isSelected = selectedSourceId == folder.id,
                                        onClick = {
                                                onSelect(folder.id)
                                                onDismiss()
                                        },
                                )
                        }
                }
        }
}

@Composable
private fun FolderRow(
        item: FolderItem,
        isSelected: Boolean,
        onClick: () -> Unit,
) {
        val bg = if (isSelected) LyraColorScheme.primary.copy(alpha = 0.12f)
        else LyraColorScheme.surfaceVariant.copy(alpha = 0.4f)
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(bg, RoundedCornerShape(12.dp))
                        .clickable(onClick = onClick)
                        .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
        ) {
                Box(modifier = Modifier.size(32.dp), contentAlignment = Alignment.Center) {
                        Text(text = item.emoji, fontSize = 22.sp)
                }
                Text(
                        text = item.title,
                        fontSize = 16.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                        color = if (isSelected) LyraColorScheme.primary else LyraColorScheme.onSurface,
                        modifier = Modifier
                                .padding(start = 12.dp)
                                .weight(1f),
                )
                Text(
                        text = "${item.count}",
                        fontSize = 14.sp,
                        color = LyraColorScheme.onSurfaceVariant,
                        fontWeight = FontWeight.Normal,
                )
        }
}
