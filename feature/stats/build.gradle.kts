import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidLibrary)
        alias(libs.plugins.composeMultiplatform)
        alias(libs.plugins.composeCompiler)
        alias(libs.plugins.ksp)
        alias(libs.plugins.androidx.room)
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
                        baseName = "StatsFeature"
                        isStatic = true
                }
        }

        sourceSets {
                androidMain.dependencies {
                        implementation(libs.koin.android)
                        implementation(libs.androidx.room.sqlite.wrapper)
                }

                commonMain.dependencies {
                        implementation(project(":core"))
                        implementation(project(":feature:listening_history"))
                        implementation(project(":feature:lyrics"))
                        implementation(project(":feature:learn_words"))
                        implementation(project(":feature:add_words_extraction"))
                        implementation(project(":feature:user_settings"))

                        implementation(libs.runtime)
                        implementation(libs.foundation)
                        implementation(libs.material3)
                        implementation(libs.material.icons.extended)
                        implementation(compose.ui)
                        implementation(compose.components.resources)

                        implementation(libs.voyager.navigator)
                        implementation(libs.voyager.screenmodel)
                        implementation(libs.voyager.koin)

                        implementation(libs.kotlinx.coroutines.core)
                        implementation(libs.kermit)
                        implementation(libs.koin.core)
                        implementation(libs.koin.compose)
                        implementation(libs.androidx.room.runtime)
                        implementation(libs.androidx.sqlite.bundled)
                }

                commonTest.dependencies {
                        implementation(libs.kotlin.test)
                }
        }
}

android {
        namespace = "com.arno.lyramp.feature.stats"
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

room {
        schemaDirectory("$projectDir/schemas")
}

compose.resources {
        publicResClass = false
        packageOfResClass = "com.arno.lyramp.feature.stats.resources"
        generateResClass = auto
}
