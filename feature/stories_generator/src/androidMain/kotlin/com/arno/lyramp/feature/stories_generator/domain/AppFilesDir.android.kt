package com.arno.lyramp.feature.stories_generator.domain

import android.content.Context
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

actual fun getAppFilesDir(): String {
        return AppFilesDirHelper.context.filesDir.absolutePath
}

private object AppFilesDirHelper : KoinComponent {
        val context: Context by inject()
}
