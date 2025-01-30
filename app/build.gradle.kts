plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinMultiplatform)
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.devtools.ksp")
    alias(libs.plugins.compose.compiler)
    alias(libs.plugins.ktorfit)
}

kotlin {
    androidTarget()
    iosX64()
    iosArm64()
    iosSimulatorArm64()

    jvmToolchain(17)

    sourceSets {
        commonMain.dependencies {
            implementation(libs.kermit)
            implementation(libs.ksoup)

            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.kotlinx.datetime)

            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.client.serialization)
            implementation(libs.ktor.serialization.json)
            implementation(libs.ktor.client.logging)

            implementation(libs.ktorfit)
            implementation(libs.ktorfit.call)
            implementation(libs.kotlin.inject.runtime)
            
            implementation(libs.androidx.datastore)
            implementation(libs.androidx.datastore.preferences)

            implementation(libs.kotlinx.collections.immutable)

        }

        androidMain.dependencies {
            implementation(libs.kotlinx.coroutines.android)
            implementation(libs.ktor.client.okhttp)

            implementation(libs.androidx.annotation)
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.activity.compose)

            implementation(libs.androidx.ui)
            implementation(libs.androidx.ui.graphics)
            implementation(libs.androidx.ui.tooling.preview)
            implementation(libs.androidx.material3)

            implementation(libs.androidx.lifecycle.runtime.compose)


            implementation(libs.volley)


            implementation(libs.androidx.runtime.livedata)


            implementation(libs.coil.compose)

            implementation(libs.androidx.navigation.compose)

            implementation(libs.material3)

            implementation(libs.androidx.browser)

            implementation(libs.androidx.material.icons.extended)

            implementation(libs.accompanist.systemuicontroller)


            implementation(libs.material)

            implementation(libs.androidx.material)

            implementation(libs.androidx.media3.exoplayer)
            implementation(libs.androidx.media3.exoplayer.dash)
            implementation(libs.androidx.media3.ui)
            implementation(libs.glide)
            implementation(libs.glide.compose)

            implementation(libs.coil.video)

            implementation(libs.android.image.cropper)

            // ViewModel
            implementation(libs.androidx.lifecycle.viewmodel.ktx)
            // LiveData
            implementation(libs.androidx.lifecycle.livedata.ktx)
            // Lifecycles only (without ViewModel or LiveData)
            implementation(libs.lifecycle.runtime.ktx)

            // Saved state module for ViewModel
            implementation(libs.androidx.lifecycle.viewmodel.savedstate)

            // widget
            implementation(libs.androidx.glance.appwidget)
            implementation(libs.androidx.glance.material3)
            // work Manager
            implementation(libs.androidx.work.runtime.ktx)
        }

        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

dependencies {
    implementation(platform(libs.androidx.compose.bom))

    // Annotation processor
    annotationProcessor(libs.androidx.lifecycle.compiler)
    add("kspAndroid", libs.glide.compiler)

    listOf(
        "kspAndroid",
        "kspIosX64",
        "kspIosArm64",
        "kspIosSimulatorArm64"
    ).forEach { add(it, libs.kotlin.inject.compiler.ksp) }
}

android {
    namespace = "com.daniebeler.pfpixelix"
    compileSdk = 35
    buildToolsVersion = "34.0.0"

    defaultConfig {
        applicationId = "com.daniebeler.pfpixelix"
        minSdk = 26
        targetSdk = 35
        versionCode = 25
        versionName = "3.3.1"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    buildFeatures {
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

}