package com.cxense.cxensesdk.configuration

import com.android.build.gradle.BaseExtension
import com.cxense.cxensesdk.dependencies.DependenciesPlugin
import com.cxense.cxensesdk.ktlint.KtlintConfigPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.jetbrains.kotlin.gradle.dsl.KotlinAndroidProjectExtension
import org.jetbrains.kotlin.gradle.plugin.KotlinAndroidPluginWrapper

class CommonAndroidConfigurationPlugin : Plugin<Project> {
    override fun apply(target: Project) = target.run {
        apply<DependenciesPlugin>()
        configureKotlin()
        configureAndroid()
    }

    private fun Project.configureAndroid() {
        extensions.findByType(BaseExtension::class.java)?.apply {
            buildToolsVersion = AndroidConfig.androidBuildTools
            compileSdkVersion(AndroidConfig.androidCompileSdk)
            defaultConfig {
                minSdk = AndroidConfig.androidMinSdk
                targetSdk = AndroidConfig.androidTargetSdk
            }
            compileOptions {
                sourceCompatibility = AndroidConfig.compileSourceVersion
                targetCompatibility = AndroidConfig.compileTargetVersion
            }
        } ?: logger.warn("Can't configure Android parameters")
    }

    private fun Project.configureKotlin() {
        apply<KotlinAndroidPluginWrapper>()
        apply<KtlintConfigPlugin>()
        extensions.configure(KotlinAndroidProjectExtension::class.java) {
            explicitApi()
        }
    }
}