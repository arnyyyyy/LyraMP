package com.arno.lyramp.feature.lyrics.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.feature.translation.model.WordInfo
import com.arno.lyramp.feature.translation.model.TranslationResult
import com.arno.lyramp.feature.translation.domain.TranslationState
import com.arno.lyramp.feature.translation.domain.TranslationRepository
import com.arno.lyramp.feature.translation.speech.TranslationSpeechController
import kotlinx.coroutines.launch
import org.koin.compose.koinInject

@Composable
internal fun LyricsWord(word: String) {
        var showTranslation by remember { mutableStateOf(false) }

        Text(
                text = word,
                fontSize = 17.sp,
                fontWeight = FontWeight.Normal,
                color = Color(0xFF2C3E50),
                lineHeight = 28.sp,
                softWrap = true,
                modifier = Modifier
                        .clickable { if (word.isNotBlank()) showTranslation = true }
                        .padding(end = 5.dp, bottom = 2.dp)
        )

        if (showTranslation) {
                ShowWordTranslation(
                        word = word,
                        onDismiss = { showTranslation = false }
                )
        }
}

@Composable
private fun ShowWordTranslation(
        word: String,
        onDismiss: () -> Unit
) {
        val translationRepository: TranslationRepository = koinInject()
        val coroutineScope = rememberCoroutineScope()

        var translationWithLang by remember { mutableStateOf(TranslationResult(null, null)) }
        var isLoadingAudio by remember { mutableStateOf(false) }
        var isPlayingSource by remember { mutableStateOf(false) }

        val speechController = remember { TranslationSpeechController() }

        DisposableEffect(Unit) {
                onDispose {
                        speechController.stop()
                }
        }

        LaunchedEffect(word) {
                val result = translationRepository.translateWord(word)
                translationWithLang = when (result) {
                        is TranslationState.Success -> result.translationWithLang
                        is TranslationState.Error -> TranslationResult(
                                "Ошибка: ${result.message}",
                                null
                        )

                        else -> TranslationResult("Не найдено", null)
                }
        }

        AlertDialog(
                onDismissRequest = onDismiss,
                title = { Text(word) },
                text = {
                        Column {
                                if (translationWithLang.translation != null) {
                                        Text(translationWithLang.translation!!)
                                        if (isLoadingAudio) {
                                                Spacer(Modifier.height(8.dp))
                                                CircularProgressIndicator(
                                                        modifier = Modifier.size(16.dp),
                                                        strokeWidth = 2.dp
                                                )
                                                Text("Загружаем аудио...", style = MaterialTheme.typography.labelSmall)
                                        }
                                } else {
                                        CircularProgressIndicator(
                                                modifier = Modifier.size(20.dp),
                                                strokeWidth = 2.dp
                                        )
                                        Spacer(Modifier.height(8.dp))
                                        Text("Загружаем перевод...")
                                }
                        }
                },
                dismissButton = {
                        Button(
                                onClick = {
                                        if (isPlayingSource) {
                                                speechController.stop()
                                                isPlayingSource = false
                                                return@Button
                                        }

                                        isLoadingAudio = true
                                        coroutineScope.launch {
                                                val wordInfo = WordInfo(
                                                        word = word,
                                                        translation = translationWithLang.translation,
                                                        sourceLang = translationWithLang.sourceLanguage
                                                )

                                                val filePath = translationRepository.getSourceSpeechFilePath(wordInfo)
                                                if (filePath != null) {
                                                        speechController.play(filePath)
                                                        isPlayingSource = true
                                                } else {
                                                        isPlayingSource = false
                                                }

                                                isLoadingAudio = false
                                        }
                                },
                                enabled = !isLoadingAudio && translationWithLang.translation != null
                        ) {
                                Text("🔊 ")
                        }
                },
                confirmButton = {
                        Button(onClick = onDismiss) {
                                Text("Ок")
                        }
                }
        )
}
