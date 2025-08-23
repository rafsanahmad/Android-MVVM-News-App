/*
 * *
 *  * Created by Rafsan Ahmad on 9/27/21, 5:30 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")

}

android {
    namespace = "com.rafsan.newsapp"
    compileSdk = Deps.Versions.compile_sdk

    buildFeatures {
        compose = true
        buildConfig = true
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Deps.Versions.composeCompiler
    }

    defaultConfig {
        applicationId = "com.rafsan.newsapp"
        minSdk = Deps.Versions.min_sdk
        targetSdk = Deps.Versions.target_sdk
        versionCode = Deps.Versions.app_version_code
        versionName = Deps.Versions.app_version_name
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        javaCompileOptions {
            annotationProcessorOptions {
                // Refer https://developer.android.com/jetpack/androidx/releases/room#compiler-options
                arguments(
                    mapOf(
                        "room.schemaLocation" to "$projectDir/schemas",
                        "room.incremental" to "true",
                        "room.expandProjection" to "true"
                    )
                )
            }
        }
    }
    flavorDimensions("default")

    productFlavors {
        create("prod") {
            applicationId = "com.rafsan.newsapp"
        }
        create("dev") {
            applicationId = "com.rafsan.newsapp.dev"
        }
    }
    buildTypes {
        getByName("debug") {
            applicationIdSuffix = ".debug"
        }
        getByName("release") {
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    testBuildType = "debug"

    packaging {
        resources.excludes += "/META-INF/{AL2.0,LGPL2.1}"
        resources.excludes += "META-INF/LICENSE*" // Changed to use a wildcard
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //App Compat, layout, Core
    implementation(project(":core"))
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":feature_news"))
    implementation(project(":feature_favorite"))
    implementation(project(":feature_search"))
    implementation(project(":feature_details"))
    implementation(project(":feature_source"))

    implementation(Deps.AndroidX.appCompat)
    implementation(Deps.AndroidX.ktx_core)
    implementation(Deps.Google.material)

    // Activity Compose
    implementation(Deps.AndroidX.activity_compose)

    // Compose
    implementation(platform(Deps.Compose.bom))
    implementation(Deps.Compose.ui)
    implementation(Deps.Compose.uiGraphics)
    implementation(Deps.Compose.uiToolingPreview)
    implementation(Deps.Compose.material3)
    debugImplementation(Deps.Compose.uiTooling)

    // Coroutines
    implementation(Deps.Coroutines.core)
    implementation(Deps.Coroutines.android)

    //Dagger - Hilt
    implementation(Deps.Hilt.android)
    kapt(Deps.Hilt.android_compiler)

    // Timber
    implementation(Deps.Timber.timber)

    // Navigation Compose
    implementation(Deps.Navigation.navigationCompose)

    // Paging Compose
    implementation(Deps.Paging.runtime)
    implementation(Deps.Paging.compose)

    // Coil
    implementation(Deps.Coil.compose)

    //Testing dependencies
    testImplementation(Deps.junit)
    testImplementation(Deps.Test.Mockito.core)
    testImplementation(Deps.Test.Mockito.inline)
    testImplementation(Deps.Test.Mockito.kotlin)
    testImplementation(Deps.junit)
    testImplementation(Deps.Test.truth)
    testImplementation(Deps.Coroutines.test)
    testImplementation(Deps.OkHttp.mockWebServer)
    androidTestImplementation(Deps.Test.junit)
    androidTestImplementation(Deps.Test.junitKtx)
    androidTestImplementation(Deps.Test.coreKtx)
    androidTestImplementation(Deps.Coroutines.test) {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-debug")
    }
    androidTestImplementation(platform(Deps.Compose.bom))
    androidTestImplementation(Deps.Compose.uiTestJunit4)
    debugImplementation(Deps.Compose.uiTestManifest)
}