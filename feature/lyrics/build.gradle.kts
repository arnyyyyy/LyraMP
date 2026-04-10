import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidLibrary)
        alias(libs.plugins.composeMultiplatform)
        alias(libs.plugins.composeCompiler)
        alias(libs.plugins.ksp)
        alias(libs.plugins.serialization)
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
                        baseName = "LyricsFeature"
                        isStatic = true
                }
        }

        sourceSets {
                androidMain.dependencies {
                        implementation(libs.koin.android)
                        implementation(libs.androidx.room.sqlite.wrapper)
                }

                iosMain.dependencies {
                        implementation(libs.jetbrains.kotlinx.coroutines.core)
                }

                commonMain.dependencies {
                        implementation(project(":core"))
                        implementation(project(":feature:authorization"))
                        implementation(project(":feature:translation"))
                        implementation(project(":feature:user_settings"))

                        implementation(compose.runtime)
                        implementation(compose.foundation)
                        implementation(compose.material3)
                        implementation(compose.components.resources)
                        implementation(libs.androidx.sqlite.bundled)
                        implementation(libs.voyager.koin)
                        implementation(libs.ktor.client.core)
                        implementation(libs.ktor.client.content.negotiation)
                        implementation(libs.ktor.serialization.kotlinx.json)
                        implementation(libs.kotlinx.serialization.json)
                        implementation(libs.multiplatform.settings)
                        implementation(libs.kotlinx.coroutines.core)
                        implementation(libs.multiplatform.settings.no.arg)
                        implementation(libs.koin.core)
                        implementation(libs.koin.compose)
                        implementation(libs.korio)
                        implementation(libs.basic.sound)
                        implementation(libs.okio)
                }

                commonTest.dependencies {
                        implementation(libs.kotlin.test)
                }
        }
}

android {
        namespace = "com.arno.lyramp.feature.lyrics"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        defaultConfig {
                minSdk = libs.versions.android.minSdk.get().toInt()
        }

        compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
        }
}

dependencies {
        add("kspAndroid", libs.androidx.room.compiler)
        add("kspIosSimulatorArm64", libs.androidx.room.compiler)
        add("kspIosArm64", libs.androidx.room.compiler)
}

compose.resources {
        publicResClass = false
        packageOfResClass = "com.arno.lyramp.feature.lyrics.resources"
        generateResClass = auto
}