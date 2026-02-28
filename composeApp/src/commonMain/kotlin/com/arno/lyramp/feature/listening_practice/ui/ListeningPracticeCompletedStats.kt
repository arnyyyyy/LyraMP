package com.arno.lyramp.feature.listening_practice.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import lyramp.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
internal fun ListeningPracticeLineStat(text: String, userInput: String, isCorrect: Boolean) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(
                                if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                RoundedCornerShape(8.dp)
                        )
                        .border(
                                1.dp,
                                if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                                RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
        ) {
                Text(
                        text = if (isCorrect) stringResource(Res.string.practice_correct_mark) else stringResource(Res.string.practice_incorrect_mark),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = stringResource(Res.string.practice_correct_prefix, text),
                        fontSize = 13.sp,
                        color = Color(0xFF2C3E50)
                )
                if (!isCorrect && userInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = stringResource(Res.string.practice_user_wrote_prefix, userInput),
                                fontSize = 13.sp,
                                color = Color(0xFF7F8C8D),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                }
        }
}


@Composable
internal fun LineResultCard(text: String, userInput: String, isCorrect: Boolean) {
        Column(
                modifier = Modifier
                        .fillMaxWidth()
                        .background(
                                if (isCorrect) Color(0xFFE8F5E9) else Color(0xFFFFEBEE),
                                RoundedCornerShape(8.dp)
                        )
                        .border(
                                1.dp,
                                if (isCorrect) Color(0xFF4CAF50) else Color(0xFFF44336),
                                RoundedCornerShape(8.dp)
                        )
                        .padding(12.dp)
        ) {
                Text(
                        text = if (isCorrect) stringResource(Res.string.practice_correct_mark) else stringResource(Res.string.practice_incorrect_mark),
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (isCorrect) Color(0xFF2E7D32) else Color(0xFFC62828)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                        text = stringResource(Res.string.practice_correct_prefix, text),
                        fontSize = 13.sp,
                        color = Color(0xFF2C3E50)
                )
                if (!isCorrect && userInput.isNotBlank()) {
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                                text = stringResource(Res.string.practice_user_wrote_prefix, userInput),
                                fontSize = 13.sp,
                                color = Color(0xFF7F8C8D),
                                fontStyle = androidx.compose.ui.text.font.FontStyle.Italic
                        )
                }
        }
}
