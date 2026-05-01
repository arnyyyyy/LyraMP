package com.arno.lyramp.feature.translation.domain

private val russianLetters = "абвгдеёжзийклмнопрстуфхцчшщъыьэюя".toSet()

fun isNativeText(text: String): Boolean {
        val letters = text.filter { it.isLetter() }
        val cyrillicLetters = letters.filter { it in '\u0400'..'\u052F' }

        if (cyrillicLetters.length < MIN_CYRILLIC_LETTERS) return false
        if (cyrillicLetters.any { it.lowercaseChar() !in russianLetters }) return false

        return cyrillicLetters.length * 2 >= letters.length
}

private const val MIN_CYRILLIC_LETTERS = 2
