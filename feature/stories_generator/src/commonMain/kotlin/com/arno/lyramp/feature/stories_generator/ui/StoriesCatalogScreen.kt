package com.arno.lyramp.feature.stories_generator.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.stories_generator.model.GeneratedStory
import com.arno.lyramp.feature.stories_generator.presentation.CatalogUiState
import com.arno.lyramp.feature.stories_generator.presentation.StoriesCatalogScreenModel
import com.arno.lyramp.feature.stories_generator.resources.Res
import com.arno.lyramp.feature.stories_generator.resources.catalog_create_own
import com.arno.lyramp.feature.stories_generator.resources.catalog_empty_subtitle
import com.arno.lyramp.feature.stories_generator.resources.catalog_empty_title
import com.arno.lyramp.feature.stories_generator.resources.catalog_generating_in_background
import com.arno.lyramp.feature.stories_generator.resources.catalog_not_enough_words_subtitle
import com.arno.lyramp.feature.stories_generator.resources.catalog_not_enough_words_title
import com.arno.lyramp.feature.stories_generator.resources.catalog_subtitle
import com.arno.lyramp.feature.stories_generator.resources.catalog_title
import com.arno.lyramp.ui.EmptyStateCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.MainFeatureScaffold
import com.arno.lyramp.ui.theme.LyraColorScheme
import org.jetbrains.compose.resources.stringResource

object StoriesCatalogScreen : Screen {
        @Composable
        override fun Content() {
                val screenModel = getScreenModel<StoriesCatalogScreenModel>()
                val navigator = LocalNavigator.currentOrThrow
                val uiState by screenModel.uiState.collectAsState()

                MainFeatureScaffold(
                        icon = "📚",
                        title = stringResource(Res.string.catalog_title),
                        subtitle = stringResource(Res.string.catalog_subtitle),
                        onBack = { navigator.pop() },
                ) {
                        Column(modifier = Modifier.fillMaxSize()) {
                                Box(
                                        modifier = Modifier
                                                .fillMaxWidth()
                                                .weight(1f)
                                ) {
                                        when (val state = uiState) {
                                                is CatalogUiState.Loading ->
                                                        LoadingCard(message = "")

                                                is CatalogUiState.NotEnoughWords ->
                                                        EmptyStateCard(
                                                                icon = "📝",
                                                                title = stringResource(Res.string.catalog_not_enough_words_title),
                                                                subtitle = stringResource(
                                                                        Res.string.catalog_not_enough_words_subtitle,
                                                                        state.current,
                                                                        state.needed
                                                                ),
                                                        )

                                                is CatalogUiState.Empty ->
                                                        EmptyStateCard(
                                                                icon = "✨",
                                                                title = stringResource(Res.string.catalog_empty_title),
                                                                subtitle = if (state.isGenerating)
                                                                        stringResource(Res.string.catalog_generating_in_background)
                                                                else
                                                                        stringResource(Res.string.catalog_empty_subtitle),
                                                        )

                                                is CatalogUiState.Items ->
                                                        CatalogList(
                                                                stories = state.stories,
                                                                isGenerating = state.isGenerating,
                                                                onStoryClick = { story ->
                                                                        screenModel.onStoryOpened(story.id)
                                                                        navigator.push(StoryDetailScreen(story.id))
                                                                }
                                                        )
                                        }
                                }

                                Spacer(modifier = Modifier.height(12.dp))

                                LyraFilledButton(
                                        text = "✍️ " + stringResource(Res.string.catalog_create_own),
                                        onClick = { navigator.push(StoryGeneratorVoyagerScreen) },
                                        modifier = Modifier.fillMaxWidth()
                                )

                                Spacer(modifier = Modifier.height(8.dp))
                        }
                }
        }
}

@Composable
private fun CatalogList(
        stories: List<GeneratedStory>,
        isGenerating: Boolean,
        onStoryClick: (GeneratedStory) -> Unit
) {
        LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                if (isGenerating) {
                        item { GeneratingBanner() }
                }
                items(stories, key = { it.id }) { story ->
                        StoryCard(story = story, onClick = { onStoryClick(story) })
                }
        }
}

@Composable
private fun GeneratingBanner() {
        Row(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(LyraColorScheme.surface, RoundedCornerShape(12.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(12.dp))
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
                CircularProgressIndicator(
                        modifier = Modifier.padding(end = 4.dp),
                        strokeWidth = 2.dp
                )
                Text(
                        text = stringResource(Res.string.catalog_generating_in_background),
                        fontSize = 13.sp,
                        color = LyraColorScheme.onSurfaceVariant
                )
        }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun StoryCard(
        story: GeneratedStory,
        onClick: () -> Unit
) {
        val borderColor = if (!story.isRead) LyraColorScheme.primary else LyraColorScheme.outline
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, borderColor, RoundedCornerShape(16.dp))
                        .clickable(onClick = onClick)
                        .padding(16.dp)
        ) {
                Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                ) {
                        Text(
                                text = "${story.genre.emoji} ${story.genre.displayName}",
                                fontSize = 13.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = LyraColorScheme.primary
                        )
                        if (!story.isRead) {
                                Box(
                                        modifier = Modifier
                                                .background(LyraColorScheme.primary, RoundedCornerShape(50))
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                ) {
                                        Text(
                                                text = "NEW",
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = LyraColorScheme.onPrimary
                                        )
                                }
                        }
                }
                Spacer(Modifier.height(8.dp))
                Text(
                        text = story.title,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = LyraColorScheme.onSurface,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(8.dp))
                FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                        story.wordsUsed.take(6).forEach { w ->
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
