package com.arno.lyramp.feature.learn_words.domain.usecase

import com.arno.lyramp.feature.learn_words.data.LearnWordDao

class GetAllUserWordStringsUseCase internal constructor(
        private val dao: LearnWordDao
) {
        suspend operator fun invoke(sourceLang: String) = dao.getAllWordStrings(sourceLang).toSet()
}
