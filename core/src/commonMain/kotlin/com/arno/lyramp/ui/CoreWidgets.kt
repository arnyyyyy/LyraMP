package com.arno.lyramp.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arno.lyramp.ui.theme.LyraColorScheme
import com.arno.lyramp.ui.theme.LyraColors
import com.arno.lyramp.ui.theme.LyraTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun LoadingCard(message: String) {
        Box(
                modifier = Modifier.fillMaxWidth(0.80f)
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(40.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator(
                                modifier = Modifier.size(48.dp),
                                color = LyraColorScheme.primary
                        )
                        Spacer(modifier = Modifier.height(20.dp))
                        Text(
                                text = message,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Medium,
                                color = LyraColorScheme.onSurface,
                                textAlign = TextAlign.Center
                        )
                }
        }
}


@Preview(showBackground = true)
@Composable
fun PreviewLoadingCard() {
        LyraTheme {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        LoadingCard(message = "Загрузка данных...")
                }
        }
}


@Composable
fun ErrorCard(
        message: String,
        onRetry: (() -> Unit)? = null,
        retryLabel: String = "Повторить",
) {
        Box(
                modifier = Modifier.fillMaxWidth(0.80f)
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColors.ErrorCardBorder, RoundedCornerShape(16.dp))
                        .padding(40.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = "⚠️", fontSize = 48.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = message,
                                fontSize = 16.sp,
                                color = LyraColorScheme.error,
                                textAlign = TextAlign.Center
                        )
                        onRetry?.let {
                                Spacer(modifier = Modifier.height(16.dp))
                                Button(
                                        onClick = onRetry,
                                        colors = ButtonDefaults.buttonColors(containerColor = LyraColorScheme.primary),
                                        shape = RoundedCornerShape(12.dp)
                                ) {
                                        Text(text = retryLabel, fontWeight = FontWeight.SemiBold)
                                }
                        }
                }
        }
}

@Composable
fun EmptyStateCard(
        icon: String = "🔎",
        title: String,
        subtitle: String? = null,
) {
        Box(
                modifier = Modifier.fillMaxWidth(0.85f)
                        .background(LyraColorScheme.surface, RoundedCornerShape(16.dp))
                        .border(1.dp, LyraColorScheme.outline, RoundedCornerShape(16.dp))
                        .padding(40.dp),
                contentAlignment = Alignment.Center
        ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(text = icon, fontSize = 64.sp)
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                                text = title,
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                        )
                        subtitle?.let {
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                        text = subtitle,
                                        fontSize = 14.sp,
                                        color = LyraColorScheme.onSurfaceVariant,
                                        textAlign = TextAlign.Center
                                )
                        }
                }
        }
}

@Composable
fun PlayAudioButton(
        isLoading: Boolean,
        onClick: () -> Unit,
        modifier: Modifier = Modifier
) {
        IconButton(
                onClick = onClick,
                enabled = !isLoading,
                modifier = modifier.size(36.dp)
        ) {
                if (isLoading)
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 1.5.dp)
                else Text("🔊", fontSize = 16.sp)

        }
}

@Composable
fun SlowModeButton(
        isSlowMode: Boolean,
        onClick: () -> Unit,
        size: Dp = 44.dp,
) {
        Button(
                onClick = onClick,
                modifier = Modifier.size(size),
                shape = CircleShape,
                contentPadding = PaddingValues(0.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = if (isSlowMode) LyraColors.Accent else LyraColorScheme.surfaceVariant,
                        contentColor = if (isSlowMode) LyraColors.AccentOnAccent else LyraColorScheme.onSurface
                ),
                elevation = ButtonDefaults.buttonElevation(0.dp)
        ) {
                Text(text = "🐢", fontSize = 18.sp)
        }
}

@Composable
fun MainFeatureScaffold(
        icon: String,
        title: String,
        subtitle: String,
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
        content: @Composable () -> Unit
) {
        Box(modifier = modifier.fillMaxSize()) {
                Box(
                        modifier = Modifier
                                .fillMaxSize()
                                .background(LyraColors.PageBackground)
                )

                Scaffold(
                        modifier = Modifier.fillMaxSize(),
                        containerColor = Color.Transparent,
                ) { paddingValues ->
                        Column(
                                modifier = Modifier
                                        .fillMaxSize()
                                        .padding(paddingValues)
                        ) {
                                ScreenTopBar(
                                        icon = icon,
                                        title = title,
                                        subtitle = subtitle,
                                        onBack = onBack
                                )

                                Box(
                                        modifier = Modifier
                                                .fillMaxSize()
                                                .padding(16.dp),
                                        contentAlignment = Alignment.Center
                                ) {
                                        content()
                                }
                        }
                }
        }
}


@Composable
fun BackButton(
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
) {
        IconButton(
                onClick = onClick,
                modifier = modifier
                        .size(40.dp)
                        .background(Color.White.copy(alpha = 0.15f), CircleShape)
        ) {
                Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                )
        }
}

@Composable
fun ScreenTopBar(
        icon: String,
        title: String,
        subtitle: String,
        onBack: () -> Unit,
        modifier: Modifier = Modifier,
) {
        Box(
                modifier = modifier
                        .fillMaxWidth()
                        .background(LyraColorScheme.surface)
                        .border(width = 1.dp, color = LyraColorScheme.outline)
                        .padding(16.dp)
        ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(
                                onClick = onBack,
                                modifier = Modifier
                                        .size(40.dp)
                                        .background(LyraColorScheme.surfaceVariant, CircleShape)
                        ) {
                                Icon(
                                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                        contentDescription = "Back",
                                        tint = LyraColorScheme.onSurface,
                                        modifier = Modifier.size(20.dp)
                                )
                        }
                        Spacer(modifier = Modifier.width(16.dp))
                        Row(
                                modifier = Modifier.weight(1f),
                                verticalAlignment = Alignment.CenterVertically
                        ) {
                                Text(text = icon, fontSize = 24.sp)
                                Spacer(modifier = Modifier.width(10.dp))
                                Column(modifier = Modifier.weight(1f)) {
                                        Text(
                                                text = title,
                                                fontSize = 18.sp,
                                                fontWeight = FontWeight.SemiBold,
                                                color = LyraColorScheme.onSurface,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis
                                        )
                                        if (subtitle.isNotEmpty()) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                        text = subtitle,
                                                        fontSize = 13.sp,
                                                        color = LyraColorScheme.onSurfaceVariant,
                                                        maxLines = 1,
                                                        overflow = TextOverflow.Ellipsis
                                                )
                                        }
                                }
                        }
                }
        }
}

@Composable
fun LyraFilledButton(
        text: String,
        onClick: () -> Unit,
        modifier: Modifier = Modifier,
        enabled: Boolean = true,
        containerColor: Color = LyraColorScheme.primary,
        contentColor: Color = Color.White,
        height: Dp = 48.dp,
) {
        Button(
                onClick = onClick,
                modifier = modifier.height(height),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                        containerColor = containerColor,
                        contentColor = contentColor,
                ),
                enabled = enabled
        ) {
                Text(text = text, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
        }
}
