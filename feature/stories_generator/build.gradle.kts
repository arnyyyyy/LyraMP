import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
        alias(libs.plugins.kotlinMultiplatform)
        alias(libs.plugins.androidLibrary)
        alias(libs.plugins.composeMultiplatform)
        alias(libs.plugins.composeCompiler)
        alias(libs.plugins.ksp)
        alias(libs.plugins.androidx.room)
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
                        baseName = "StoriesGeneratorFeature"
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
                        implementation(libs.kotlinx.serialization.json)
                        implementation(libs.kermit)
                        implementation(libs.koin.core)
                        implementation(libs.ktor.client.core)
                        implementation(libs.llamatik)
                        implementation(libs.androidx.room.runtime)
                        implementation(libs.androidx.sqlite.bundled)
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
        packageOfResClass = "com.arno.lyramp.feature.stories_generator.resources"
        generateResClass = auto
}
