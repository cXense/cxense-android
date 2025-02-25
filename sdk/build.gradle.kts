plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.ksp)
    alias(libs.plugins.binaryCompatibility)
    alias(libs.plugins.ktlint)
    alias(libs.plugins.mavenRelease)
    alias(libs.plugins.moshiIR)
}

@Suppress("PropertyName")
val GROUP: String by project

@Suppress("PropertyName")
val VERSION_NAME: String by project

group = GROUP
version = VERSION_NAME

android {
    namespace = "io.piano.android.cxense"
    defaultConfig {
        minSdk = 21
        compileSdk = 34
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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

kotlin {
    explicitApi()
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(11))
    }
}

ktlint {
    version.set("1.4.1")
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
    compileOnly(libs.kotlinCoroutines)

    testImplementation(libs.kotlinJunit)
    testImplementation(libs.mockitoKotlin)
    testImplementation(libs.mockitoCore)
    testImplementation(libs.junit)
    testImplementation(libs.okhttpMockServer)
}
