pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "Nepali Keyboard"
include(":app")
//include(":core:core-common")
//include(":core:core-ui")
//include(":core:core-data")
//include(":feature:feature-ime")
//include(":feature:feature-transliteration")
//include(":feature:feature-dictionary")
//include(":feature:feature-settings")
