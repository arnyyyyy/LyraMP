import org.gradle.kotlin.dsl.project

rootProject.name = "LyraMP"
enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")

pluginManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositories {
        google {
            mavenContent {
                includeGroupAndSubgroups("androidx")
                includeGroupAndSubgroups("com.android")
                includeGroupAndSubgroups("com.google")
            }
        }
        mavenCentral()
    }
}

include(":composeApp")
include(":core")
include(":feature:add_words_extraction")
include(":feature:authorization")
include(":feature:translation")
include(":feature:lyrics")
include(":feature:onboarding")
include(":feature:listening_history")
include(":feature:listening_practice")
include(":feature:music_streaming")
include(":feature:learn_words")
include(":feature:user_settings")
include(":feature:stories_generator")
