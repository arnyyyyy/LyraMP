import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidLibrary)
        alias(libs.plugins.composeMultiplatform)
        alias(libs.plugins.composeCompiler)
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
                        baseName = "StoriesGeneratorFeature"
                        isStatic = true
                }
        }

        sourceSets {
                androidMain.dependencies {
                        implementation(libs.koin.android)
                }

                iosMain.dependencies {
                        implementation(libs.jetbrains.kotlinx.coroutines.core)
                }

                commonMain.dependencies {
                        implementation(project(":core"))
                        implementation(project(":feature:user_settings"))
                        implementation(project(":feature:learn_words"))

                        implementation(compose.runtime)
                        implementation(compose.foundation)
                        implementation(compose.material3)
                        implementation(compose.components.resources)

                        implementation(libs.voyager.navigator)
                        implementation(libs.voyager.screenmodel)
                        implementation(libs.voyager.koin)

                        implementation(libs.kotlinx.coroutines.core)
                        implementation(libs.kermit)
                        implementation(libs.koin.core)
                        implementation(libs.ktor.client.core)
                        implementation(libs.llamatik)
                }

                commonTest.dependencies {
                        implementation(libs.kotlin.test)
                }
        }
}

android {
        namespace = "com.arno.lyramp.feature.stories_generator"
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
        packageOfResClass = "com.arno.lyramp.feature.stories_generator.resources"
        generateResClass = auto
}
