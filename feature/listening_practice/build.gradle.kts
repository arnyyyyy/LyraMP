import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidLibrary)
        alias(libs.plugins.composeMultiplatform)
        alias(libs.plugins.composeCompiler)
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
                        baseName = "ListeningPracticeFeature"
                        isStatic = true
                }
        }

        sourceSets {
                androidMain.dependencies {
                        implementation(libs.koin.android)
                        implementation(libs.androidx.media3.exoplayer)
                        implementation(libs.androidx.media3.common)
                }

                iosMain.dependencies {
                        implementation(libs.jetbrains.kotlinx.coroutines.core)
                }

                commonMain.dependencies {
                        implementation(project(":core"))
                        implementation(project(":feature:authorization"))
                        implementation(project(":feature:lyrics"))
                        implementation(project(":feature:music_streaming"))

                        implementation(libs.runtime)
                        implementation(libs.foundation)
                        implementation(libs.material3)
                        implementation(compose.components.resources)
                        implementation(libs.voyager.navigator)
                        implementation(libs.voyager.screenmodel)
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
                        implementation(libs.androidx.lifecycle.runtimeCompose)
                }

                commonTest.dependencies {
                        implementation(libs.kotlin.test)
                }
        }
}

android {
        namespace = "com.arno.lyramp.feature.listening_practice"
        compileSdk = libs.versions.android.compileSdk.get().toInt()

        defaultConfig {
                minSdk = libs.versions.android.minSdk.get().toInt()
        }

        compileOptions {
                sourceCompatibility = JavaVersion.VERSION_11
                targetCompatibility = JavaVersion.VERSION_11
        }
}

compose.resources {
        publicResClass = false
        packageOfResClass = "com.arno.lyramp.feature.listening_practice.resources"
        generateResClass = auto
}