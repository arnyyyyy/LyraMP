package com.arno.lyramp.di

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module

@OptIn(ExperimentalSettingsImplementation::class)
actual val secureStorageModule: Module = module {
        single<Settings>(secureSettingsQualifier) {
                KeychainSettings(service = "com.arno.lyramp.auth_storage")
        }
}
