package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.lyrics.resources.Res
import com.arno.lyramp.feature.lyrics.resources.edit_text
import com.arno.lyramp.feature.lyrics.resources.enter_text
import com.arno.lyramp.feature.lyrics.resources.paste_lyrics_hint
import com.arno.lyramp.feature.lyrics.resources.save
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun LyricsTextEditor(
        initialText: String = "",
        onSubmit: (String) -> Unit,
) {
        var text by remember { mutableStateOf(initialText) }
        val isEditing = initialText.isNotBlank()

        Column(
                modifier = Modifier
                        .fillMaxWidth(0.90f)
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
        ) {
                Text(text = if (isEditing) "✏️" else "📝", fontSize = 48.sp)

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                        text = stringResource(if (isEditing) Res.string.edit_text else Res.string.enter_text),
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(16.dp))

                TextField(
                        value = text,
                        onValueChange = { text = it },
                        placeholder = {
                                Text(
                                        text = stringResource(Res.string.paste_lyrics_hint),
                                        color = LyraColorScheme.onSurfaceVariant
                                )
                        },
                        modifier = Modifier
                                .fillMaxWidth()
                                .weight(1f),
                        colors = TextFieldDefaults.colors(
                                focusedContainerColor = Color.Transparent,
                                unfocusedContainerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent,
                        ),
                        shape = RoundedCornerShape(12.dp),
                )

                Spacer(modifier = Modifier.height(16.dp))

                Button(
                        onClick = { onSubmit(text) },
                        enabled = text.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(containerColor = LyraColorScheme.primary),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.fillMaxWidth()
                ) {
                        Text(
                                text = stringResource(Res.string.save),
                                fontWeight = FontWeight.SemiBold
                        )
                }
        }
}
