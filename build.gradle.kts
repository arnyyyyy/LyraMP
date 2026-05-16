plugins {
        // this is necessary to avoid the plugins to be loaded multiple times
        // in each subproject's classloader
        alias(libs.plugins.androidApplication) apply false
        alias(libs.plugins.androidLibrary) apply false
        alias(libs.plugins.composeMultiplatform) apply false
        alias(libs.plugins.composeCompiler) apply false
        alias(libs.plugins.kotlinMultiplatform) apply false
        alias(libs.plugins.ksp) apply false
        alias(libs.plugins.androidx.room) apply false
        alias(libs.plugins.dependency.analysis)
}

subprojects {
        apply(plugin = "com.autonomousapps.dependency-analysis")

        pluginManager.withPlugin("com.android.application") {
                configurations.matching { it.name == "annotationProcessor" }.configureEach {
                        isCanBeResolved = true
                }
        }
        pluginManager.withPlugin("com.android.library") {
                configurations.matching { it.name == "annotationProcessor" }.configureEach {
                        isCanBeResolved = true
                }
        }
}
