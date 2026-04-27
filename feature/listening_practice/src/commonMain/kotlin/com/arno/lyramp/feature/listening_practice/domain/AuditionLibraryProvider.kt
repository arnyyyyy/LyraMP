package com.arno.lyramp.feature.listening_practice.domain

import com.arno.lyramp.feature.listening_practice.model.PracticeTrack

interface AuditionLibraryProvider {
        suspend fun getTracks(language: String?): List<PracticeTrack>
}
