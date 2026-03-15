package com.arno.lyramp.di

import org.koin.core.module.Module
import org.koin.core.qualifier.named

expect val secureStorageModule: Module

internal val secureSettingsQualifier = named("auth_storage")
