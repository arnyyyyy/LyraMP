package com.arno.lyramp.feature.extraction.domain.usecase

import com.arno.lyramp.feature.extraction.data.ExtractionShownWordsDao
import com.arno.lyramp.feature.extraction.data.ExtractionShownWordsEntity
import com.arno.lyramp.feature.extraction.data.ExtractionShownWordsMapper
import com.arno.lyramp.feature.extraction.domain.model.ExtractedWord
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext

internal class MarkWordsAsShownUseCase(
        private val shownWordsDao: ExtractionShownWordsDao,
        private val mapper: ExtractionShownWordsMapper
) {
        suspend operator fun invoke(words: List<ExtractedWord>) {
                withContext(Dispatchers.IO) {
                        shownWordsDao.insertAll(mapper.listToEntities(words))
                }
        }
}

class MarkWordStringsAsShownUseCase internal constructor(
        private val shownWordsDao: ExtractionShownWordsDao
) {
        suspend operator fun invoke(words: List<String>) {
                withContext(Dispatchers.IO) {
                        shownWordsDao.insertAll(words.map { ExtractionShownWordsEntity(word = it) })
                }
        }
}

// TODO: пока без репозитория, поэтому здесь диспатчеры, но потом лучше убрать
class GetShownWordsUseCase internal constructor(
        private val shownWordsDao: ExtractionShownWordsDao
) {
        suspend operator fun invoke(): Set<String> {
                return withContext(Dispatchers.IO) {
                        shownWordsDao.getAllShownWords().toSet()
                }
        }
}

//internal class IsWordShownUseCase(
//        private val shownWordsDao: ExtractionShownWordsDao
//) {
//        suspend operator fun invoke(word: String): Boolean {
//                return shownWordsDao.checkIfWordShown(word)
//        }
//}
