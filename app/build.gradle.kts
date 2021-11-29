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
    id("androidx.navigation.safeargs")
}

android {
    compileSdk = Deps.Versions.compile_sdk

    buildFeatures {
        viewBinding = true
        buildConfig = true
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    testOptions {
        unitTests.isIncludeAndroidResources = true
        animationsDisabled = true
    }

    testBuildType = "debug"

    packagingOptions {
        resources.excludes.add("META-INF/*")
        resources.excludes.add(".readme")
    }

    sourceSets {
        val test by getting
        val androidTest by getting
        test.java.srcDirs("$projectDir/src/testShared")
        androidTest.java.srcDirs("$projectDir/src/testShared")
    }

    kotlinOptions {
        jvmTarget = "1.8"
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    //App Compat, layout, Core
    implementation(Deps.AndroidX.appCompat)
    implementation(Deps.AndroidX.constraint_layout)
    implementation(Deps.AndroidX.ktx_core)

    //Material
    implementation(Deps.Google.material)

    //Room
    implementation(Deps.Room.runtime)
    implementation(Deps.Room.ktx)
    kapt(Deps.Room.compiler)

    // Activity KTX
    implementation(Deps.AndroidX.ktx_activity)

    // Lifecycle
    implementation(Deps.Lifecycle.extensions)
    implementation(Deps.Lifecycle.lifeCycleLiveData)
    implementation(Deps.Lifecycle.viewmodel)
    implementation(Deps.Lifecycle.lifeCycleRunTime)

    // Retrofit
    implementation(Deps.Retrofit.main)
    implementation(Deps.Retrofit.converterGSON)

    // OkHTTP
    implementation(Deps.OkHttp.main)
    implementation(Deps.OkHttp.logging_interceptor)

    // Coroutines
    implementation(Deps.Coroutines.core)
    implementation(Deps.Coroutines.android)

    //Dagger - Hilt
    implementation(Deps.Hilt.android)
    kapt(Deps.Hilt.android_compiler)

    //Navigation
    implementation(Deps.Navigation.navigationFragment)
    implementation(Deps.Navigation.navigationKtx)

    // Glide
    implementation(Deps.Glide.runtime)
    kapt(Deps.Glide.compiler)

    //Swipe Refresh Layout
    implementation(Deps.SwipeRefreshLayout)

    //Idling Resource
    implementation(Deps.AndroidX.Test.Espresso.idling_resource)

    //Testing dependencies
    testImplementation(Deps.junit)
    testImplementation(Deps.Test.Mockito.core)
    testImplementation(Deps.Test.Mockito.inline)
    testImplementation(Deps.Test.Mockito.kotlin)
    testImplementation(Deps.AndroidX.Test.arch_core_testing)
    testImplementation(Deps.AndroidX.Test.core)
    testImplementation(Deps.Test.robolectric)
    testImplementation(Deps.Test.truth)
    testImplementation(Deps.Coroutines.test)
    testImplementation(Deps.OkHttp.mockWebServer)
    androidTestImplementation(Deps.AndroidX.Test.arch_core_testing)
    androidTestImplementation(Deps.AndroidX.Test.junit)
    androidTestImplementation(Deps.AndroidX.Test.junitKtx)
    androidTestImplementation(Deps.AndroidX.Test.coreKtx)
    androidTestImplementation(Deps.Test.Mockito.kotlin)
    androidTestImplementation(Deps.Test.Mockito.dexMaker)
    androidTestImplementation(Deps.Coroutines.test) {
        exclude(group = "org.jetbrains.kotlinx", module = "kotlinx-coroutines-debug")
    }
    androidTestImplementation(Deps.AndroidX.Test.Espresso.core) {
        exclude(group = "org.checkerframework", module = "checker")
    }
    androidTestImplementation(Deps.AndroidX.Test.Espresso.contrib) {
        exclude(group = "org.checkerframework", module = "checker")
    }
    androidTestImplementation(Deps.AndroidX.Test.Espresso.intents) {
        exclude(group = "org.checkerframework", module = "checker")
    }
    debugImplementation(Deps.AndroidX.Test.fragmentTest) {
        exclude(group = "androidx.test", module = "monitor")
    }
    debugImplementation(Deps.AndroidX.Test.core) {
        exclude(group = "androidx.test", module = "monitor")
    }
}