package com.arno.lyramp.feature.listening_practice

fun formatTime(milliseconds: Long): String {
        val seconds = (milliseconds / 1000) % 60
        val minutes = (milliseconds / 1000) / 60
        return "${minutes}:${if (seconds < 10) "0" else ""}${seconds}"
}
