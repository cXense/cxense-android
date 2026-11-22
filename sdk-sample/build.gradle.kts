plugins {
    alias(libs.plugins.android.app)
    alias(libs.plugins.android.secrets)
}

android {
    namespace = "com.example.cxensesdk"
    defaultConfig {
        minSdk = 28
        compileSdk = 36
        targetSdk = 36
        applicationId = "com.example.cxensesdk"
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }
    buildTypes {
        named("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    buildFeatures {
        buildConfig = true
        viewBinding = true
    }
}

secrets {
    propertiesFileName = "cxense.properties"
    defaultPropertiesFileName = "cxense.defaults.properties"
}

dependencies {
    implementation(project(":sdk"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.recyclerView)
    implementation(libs.viewBindingProperty)
    implementation(libs.kotlinCoroutines)
    implementation(libs.kotlinCoroutinesAndroid)
    implementation(libs.lifecycleRuntime)
}
