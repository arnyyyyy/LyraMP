package com.arno.lyramp.feature.extraction.domain.usecase

import com.arno.lyramp.feature.extraction.data.ExtractionShownWordsDao
import com.arno.lyramp.feature.extraction.data.ExtractionShownWordsMapper
import com.arno.lyramp.feature.extraction.domain.model.ExtractedWord

internal class MarkWordsAsShownUseCase(
        private val shownWordsDao: ExtractionShownWordsDao,
        private val mapper: ExtractionShownWordsMapper
) {
        suspend operator fun invoke(words: List<ExtractedWord>) {
                shownWordsDao.insertAll(mapper.listToEntities(words))
        }
}

internal class GetShownWordsUseCase(
        private val shownWordsDao: ExtractionShownWordsDao
) {
        suspend operator fun invoke(): Set<String> {
                return shownWordsDao.getAllShownWords().toSet()
        }
}

//internal class IsWordShownUseCase(
//        private val shownWordsDao: ExtractionShownWordsDao
//) {
//        suspend operator fun invoke(word: String): Boolean {
//                return shownWordsDao.checkIfWordShown(word)
//        }
//}
