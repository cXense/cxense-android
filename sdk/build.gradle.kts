plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.ksp)
    alias(libs.plugins.binaryCompatibility)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.mavenRelease)
    alias(libs.plugins.moshiIR)
}

group = project.property("GROUP").toString()
version = project.property("VERSION_NAME").toString()

android {
    namespace = "io.piano.android.cxense"
    defaultConfig {
        minSdk = 28
        compileSdk = 36
        val authority = "CxSdkInitProvider"
        buildConfigField("String", "SDK_VERSION", """"$version"""")
        buildConfigField("String", "SDK_NAME", """"cxense"""")
        buildConfigField("String", "SDK_ENDPOINT", """"https://api.cxense.com"""")
        buildConfigField("String", "AUTHORITY", """"io.piano.android.cxense.$authority"""")

        manifestPlaceholders += "authority" to authority

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("cxensesdk.pro")
    }
    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }

    lint {
        abortOnError = false
    }
}

kotlin {
    explicitApi()
}

ktlint {
    version.set("1.8.0")
    android.set(true)
}

dependencies {
    api(libs.annotations)
    implementation(libs.startup)
    implementation(libs.googleAdsId)
    api(libs.retrofit)
    api(libs.retrofitConverter)
    implementation(libs.moshi)
    implementation(libs.moshiAdapters)
    api(libs.okhttpLogging)
    api(libs.timber)
    api(libs.kotlinCoroutines)

    testImplementation(libs.kotlinJunit)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.junit)
    testImplementation(libs.okhttpMockServer)
    testImplementation(libs.kotlinCoroutinesTest)
}
