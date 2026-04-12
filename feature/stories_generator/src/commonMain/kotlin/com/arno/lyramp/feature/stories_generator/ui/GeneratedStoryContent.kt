package com.arno.lyramp.feature.stories_generator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.stories_generator.presentation.StoryUiState
import com.arno.lyramp.feature.stories_generator.resources.Res
import com.arno.lyramp.feature.stories_generator.resources.back
import com.arno.lyramp.feature.stories_generator.resources.story_words_used
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun GeneratedStoryContent(
        state: StoryUiState.StoryGenerated,
        onBack: () -> Unit
) {
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState()),
                horizontalAlignment = Alignment.CenterHorizontally
        ) {
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                                .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                                .padding(24.dp)
                ) {
                        Column {
                                Text(
                                        text = state.streamedText,
                                        fontSize = 18.sp,
                                        lineHeight = 28.sp,
                                        color = LyraColorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(16.dp))

                                Text(
                                        text = stringResource(
                                                Res.string.story_words_used,
                                                state.story.wordsUsed.joinToString(", ") { it.word }
                                        ),
                                        fontSize = 13.sp,
                                        color = LyraColorScheme.onSurfaceVariant,
                                        modifier = Modifier.padding(top = 8.dp)
                                )
                        }
                }

                Spacer(modifier = Modifier.height(24.dp))

                StoryButton(
                        text = stringResource(Res.string.back),
                        onClick = onBack,
                        modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))
        }
}
