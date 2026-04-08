package com.arno.lyramp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

val LyraColorScheme = lightColorScheme(
        primary = LyraPrimary,
        onPrimary = LyraOnPrimary,
        error = LyraError,
        onError = LyraOnError,
        surface = LyraSurface,
        onSurface = LyraOnSurface,
        surfaceVariant = LyraSurfaceVariant,
        onSurfaceVariant = LyraOnSurfaceVariant,
        outline = LyraOutline,
)

@Composable
fun LyraTheme(content: @Composable () -> Unit) {
        MaterialTheme(
                colorScheme = LyraColorScheme,
                content = content
        )
}
