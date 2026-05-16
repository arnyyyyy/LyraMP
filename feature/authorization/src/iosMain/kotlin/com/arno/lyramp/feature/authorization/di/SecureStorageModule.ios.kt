package com.arno.lyramp.feature.authorization.di

import com.russhwolf.settings.ExperimentalSettingsImplementation
import com.russhwolf.settings.KeychainSettings
import com.russhwolf.settings.NSUserDefaultsSettings
import com.russhwolf.settings.Settings
import org.koin.core.module.Module
import org.koin.dsl.module
import platform.Foundation.NSUserDefaults

@OptIn(ExperimentalSettingsImplementation::class)
actual val secureStorageModule: Module = module {
        single<Settings>(secureSettingsQualifier) {
                KeychainSettings(service = AUTH_STORAGE_IOS_SERVICE)
        }

        single<Settings>(plainSettingsQualifier) {
                NSUserDefaultsSettings(NSUserDefaults.standardUserDefaults)
        }
}
