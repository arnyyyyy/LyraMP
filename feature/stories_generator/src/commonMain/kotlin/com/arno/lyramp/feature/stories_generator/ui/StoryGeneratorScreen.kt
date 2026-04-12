package com.arno.lyramp.feature.stories_generator.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.koin.getScreenModel
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.arno.lyramp.feature.stories_generator.presentation.StoryScreenModel
import com.arno.lyramp.feature.stories_generator.presentation.StoryUiState
import com.arno.lyramp.feature.stories_generator.resources.Res
import com.arno.lyramp.feature.stories_generator.resources.retry
import com.arno.lyramp.feature.stories_generator.resources.story_empty_subtitle
import com.arno.lyramp.feature.stories_generator.resources.story_empty_title
import com.arno.lyramp.feature.stories_generator.resources.story_generating
import com.arno.lyramp.feature.stories_generator.resources.story_subtitle
import com.arno.lyramp.feature.stories_generator.resources.story_title
import com.arno.lyramp.ui.EmptyStateCard
import com.arno.lyramp.ui.ErrorCard
import com.arno.lyramp.ui.LoadingCard
import com.arno.lyramp.ui.LyraFilledButton
import com.arno.lyramp.ui.MainFeatureScaffold
import org.jetbrains.compose.resources.stringResource

object StoryGeneratorVoyagerScreen : Screen {
        @Composable
        override fun Content() {
                val screenModel = getScreenModel<StoryScreenModel>()
                val navigator = LocalNavigator.currentOrThrow
                val uiState by screenModel.uiState.collectAsState()
                val modelState by screenModel.modelState.collectAsState()
                val activeModel by screenModel.activeModel.collectAsState()

                MainFeatureScaffold(
                        icon = "📖",
                        title = stringResource(Res.string.story_title),
                        subtitle = stringResource(Res.string.story_subtitle),
                        onBack = { navigator.pop() },
                ) {
                        Column(
                                modifier = Modifier.fillMaxSize(),
                                horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                                val showModelCard = uiState is StoryUiState.Idle || uiState is StoryUiState.Ready
                                if (showModelCard) {
                                        ModelDownloadCard(
                                                modelState = modelState,
                                                activeModel = activeModel,
                                                onDownload = { screenModel.downloadModel(it) },
                                                onDelete = { screenModel.deleteModel() }
                                        )
                                        Spacer(modifier = Modifier.height(16.dp))
                                }

                                Box(
                                        modifier = Modifier.fillMaxSize(),
                                        contentAlignment = Alignment.Center
                                ) {
                                        when (val state = uiState) {
                                                is StoryUiState.Idle -> {
                                                        EmptyStateCard(
                                                                icon = "📝",
                                                                title = stringResource(Res.string.story_empty_title),
                                                                subtitle = stringResource(Res.string.story_empty_subtitle),
                                                        )
                                                }

                                                is StoryUiState.Ready -> {
                                                        ChooseWordsContent(
                                                                state = state,
                                                                onToggleSelectAll = { screenModel.toggleSelectAll() },
                                                                onToggleWord = { screenModel.toggleSelectWord(it) },
                                                                onGenerate = { screenModel.generateStory() }
                                                        )
                                                }

                                                is StoryUiState.Generating -> {
                                                        LoadingCard(message = stringResource(Res.string.story_generating))
                                                }

                                                is StoryUiState.StoryGenerated -> {
                                                        GeneratedStoryContent(
                                                                state = state,
                                                                onBack = { screenModel.backToWords() }
                                                        )
                                                }

                                                is StoryUiState.Error -> {
                                                        ErrorCard(
                                                                message = state.message,
                                                                retryLabel = stringResource(Res.string.retry),
                                                                onRetry = { screenModel.backToWords() },
                                                        )
                                                }
                                        }
                                }
                        }
                }
        }
}

@Composable
internal fun StoryButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        containerColor: Color = MaterialTheme.colorScheme.primary
) {
        LyraFilledButton(
                text = text, onClick = onClick, modifier = modifier, enabled = enabled, containerColor = containerColor,
        )
}
