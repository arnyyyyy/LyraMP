package com.arno.lyramp.feature.stories_generator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.stories_generator.presentation.StoryDetailScreenModel
import com.arno.lyramp.feature.stories_generator.presentation.StoryDetailUiState
import com.arno.lyramp.feature.stories_generator.resources.Res
import com.arno.lyramp.feature.stories_generator.resources.story_detail_genre
import com.arno.lyramp.feature.stories_generator.resources.story_detail_words
import com.arno.lyramp.feature.stories_generator.resources.story_not_found
import com.arno.lyramp.ui.EmptyStateCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.MainFeatureScaffold
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.koin.core.parameter.parametersOf
import org.jetbrains.compose.resources.stringResource

data class StoryDetailScreen(val storyId: Long) : Screen {
        @Composable
        override fun Content() {
                val screenModel = getScreenModel<StoryDetailScreenModel> { parametersOf(storyId) }
                val navigator = LocalNavigator.currentOrThrow
                val uiState by screenModel.uiState.collectAsState()

                MainFeatureScaffold(
                        icon = "📖",
                        title = when (val s = uiState) {
                                is StoryDetailUiState.Loaded -> s.story.title
                                else -> ""
                        },
                        subtitle = when (val s = uiState) {
                                is StoryDetailUiState.Loaded -> "${s.story.genre.emoji} ${s.story.genre.displayName}"
                                else -> ""
                        },
                        onBack = { navigator.pop() },
                ) {
                        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.TopStart) {
                                when (val state = uiState) {
                                        StoryDetailUiState.Loading ->
                                                LoadingCard(message = "")

                                        StoryDetailUiState.NotFound ->
                                                EmptyStateCard(
                                                        icon = "🚫",
                                                        title = stringResource(Res.string.story_not_found),
                                                        subtitle = "",
                                                )

                                        is StoryDetailUiState.Loaded ->
                                                StoryBody(state = state)
                                }
                        }
                }
        }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoryBody(state: StoryDetailUiState.Loaded) {
        val story = state.story
        Column(
                modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(rememberScrollState())
        ) {
                Box(
                        modifier = Modifier
                                .fillMaxWidth()
                                .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                                .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                                .padding(20.dp)
                ) {
                        Column {
                                Text(
                                        text = stringResource(
                                                Res.string.story_detail_genre,
                                                "${story.genre.emoji} ${story.genre.displayName}"
                                        ),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LyraColorScheme.primary
                                )
                                Spacer(Modifier.height(12.dp))
                                Text(
                                        text = story.text,
                                        fontSize = 17.sp,
                                        lineHeight = 26.sp,
                                        color = LyraColorScheme.onSurface
                                )
                                Spacer(Modifier.height(20.dp))
                                Text(
                                        text = stringResource(Res.string.story_detail_words),
                                        fontSize = 13.sp,
                                        fontWeight = FontWeight.SemiBold,
                                        color = LyraColorScheme.onSurfaceVariant
                                )
                                Spacer(Modifier.height(8.dp))
                                FlowRow(
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        verticalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                        story.wordsUsed.forEach { w ->
                                                Box(
                                                        modifier = Modifier
                                                                .background(
                                                                        LyraColorScheme.primary.copy(alpha = 0.1f),
                                                                        RoundedCornerShape(50)
                                                                )
                                                                .padding(horizontal = 10.dp, vertical = 4.dp)
                                                ) {
                                                        Text(
                                                                text = w.word,
                                                                fontSize = 12.sp,
                                                                color = LyraColorScheme.primary
                                                        )
                                                }
                                        }
                                }
                        }
                }
                Spacer(Modifier.height(16.dp))
        }
}
