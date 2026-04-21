import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidApplication)
        alias(libs.plugins.composeMultiplatform)
        alias(libs.plugins.composeCompiler)
        alias(libs.plugins.ksp)
        alias(libs.plugins.androidx.room)
        kotlin("plugin.serialization") version "2.0.20"
}

kotlin {

        compilerOptions {
                freeCompilerArgs.add("-Xexpect-actual-classes")
        }


        androidTarget {
                compilerOptions {
                        jvmTarget.set(JvmTarget.JVM_11)
                }
        }

        listOf(
                iosArm64(),
                iosSimulatorArm64()
        ).forEach { iosTarget ->
                iosTarget.binaries.framework {
                        baseName = "ComposeApp"
                        isStatic = true
                        linkerOpts("-framework", "Security")
                        linkerOpts("-framework", "AVFoundation")
                }
        }

        sourceSets {
                androidMain.dependencies {
                        implementation(compose.preview)
                        implementation(libs.androidx.activity.compose)
                        implementation(libs.ktor.client.okhttp)
                        implementation(libs.okhttp)
                        implementation(libs.androidx.media3.exoplayer)
                        implementation(libs.androidx.media3.ui)
                        implementation(libs.androidx.media3.common)
                        implementation(libs.koin.android)
                        implementation(libs.androidx.room.sqlite.wrapper)
                        implementation(libs.androidx.security.crypto)

                }

                iosMain.dependencies {
                        implementation(libs.ktor.client.darwin)
                        implementation(libs.krypto)
                        implementation(libs.jetbrains.kotlinx.coroutines.core)
                }

                commonMain.dependencies {
                        implementation(project(":core"))
                        implementation(project(":feature:authorization"))
                        implementation(project(":feature:add_words_extraction"))
                        implementation(project(":feature:translation"))
                        implementation(project(":feature:lyrics"))
                        implementation(project(":feature:onboarding"))
                        implementation(project(":feature:listening_history"))
                        implementation(project(":feature:learn_words"))
                        implementation(project(":feature:listening_practice"))
                        implementation(project(":feature:music_streaming"))
                        implementation(project(":feature:user_settings"))
                        implementation(project(":feature:stories_generator"))
                        implementation(project(":feature:album_suggestion"))

                        implementation(compose.runtime)
                        implementation(compose.foundation)
                        implementation(compose.material3)
                        implementation(compose.materialIconsExtended)
                        implementation(compose.ui)
                        implementation(compose.components.resources)
                        implementation(compose.components.uiToolingPreview)
                        implementation(libs.androidx.lifecycle.viewmodelCompose)
                        implementation(libs.androidx.lifecycle.runtimeCompose)
                        implementation(libs.voyager.navigator)
                        implementation(libs.voyager.tab.navigator)
                        implementation(libs.voyager.screenmodel)
                        implementation(libs.voyager.transitions)
                        implementation(libs.voyager.koin)
                        implementation(libs.ktor.client.core)
                        implementation(libs.ktor.client.content.negotiation)
                        implementation(libs.ktor.serialization.kotlinx.json)
                        implementation(libs.kotlinx.serialization.json)
                        implementation(libs.multiplatform.settings)
                        implementation(libs.kotlinx.coroutines.core)
                        implementation(libs.kermit)
                        implementation(libs.krypto)
                        implementation(libs.multiplatform.settings.no.arg)
                        implementation(libs.koin.core)
                        implementation(libs.koin.compose)
                        implementation(libs.basic.sound)
                        implementation(libs.okio)
                        implementation(libs.androidx.room.runtime)
                        implementation(libs.androidx.sqlite.bundled)
                        implementation(libs.coil3.compose)
                        implementation(libs.coil3.network.ktor3)
                }
                commonTest.dependencies {
                        implementation(libs.kotlin.test)
                }
        }
}

android {
        namespace = "com.arno.lyramp"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        defaultConfig {
                applicationId = "com.arno.lyramp"
                minSdk = libs.versions.android.minSdk.get().toInt()
                targetSdk = libs.versions.android.targetSdk.get().toInt()
                versionCode = 1
                versionName = "1.0"
        }
        packaging {
                resources {
                        excludes += "/META-INF/{AL2.0,LGPL2.1}"
                }
        }
        buildTypes {
                getByName("release") {
                        isMinifyEnabled = false
                }
        }
        compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
        }
}

dependencies {
        debugImplementation(compose.uiTooling)
        add("kspAndroid", libs.androidx.room.compiler)
        add("kspIosSimulatorArm64", libs.androidx.room.compiler)
        add("kspIosArm64", libs.androidx.room.compiler)
}

room {
        schemaDirectory("$projectDir/schemas")
}
