plugins {
    id("dependencies-updater")
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.android.app) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.ksp) apply false
    alias(libs.plugins.binaryCompatibility) apply false
    alias(libs.plugins.ktlint) apply false
    alias(libs.plugins.mavenRelease) apply false
    alias(libs.plugins.moshiIR) apply false
    alias(libs.plugins.android.secrets) apply false
}
