plugins {
    alias(libs.plugins.jetbrainsKotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvmToolchain(17)
}

android {
    namespace = "com.daniebeler.pfpixelix.shared"
    compileSdk = 35
    buildToolsVersion = "34.0.0"

    defaultConfig {
        minSdk = 26
    }
}