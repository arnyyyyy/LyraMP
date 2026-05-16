package com.arno.lyramp.feature.authorization.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named

expect val secureStorageModule: Module

internal const val AUTH_STORAGE_NAME = "auth_storage"
internal const val AUTH_STORAGE_IOS_SERVICE = "com.arno.lyramp.auth_storage"
internal val secureSettingsQualifier = named(AUTH_STORAGE_NAME)
internal val plainSettingsQualifier = named("plain_settings")

