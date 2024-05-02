pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

plugins {
    id("com.gradle.develocity") version "3.17.2"
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

develocity {
    buildScan {
        publishing.onlyIf { false }
    }
}

includeBuild("gradle/plugins")

include(":sdk", ":sdk-sample")
