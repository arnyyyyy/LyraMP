package com.arno.lyramp.feature.listening_practice.domain

internal class CheckAnswerUseCase {
        operator fun invoke(userInput: String, expected: String) = AnswerMatcher.isAcceptable(expected = expected, actual = userInput)
}
