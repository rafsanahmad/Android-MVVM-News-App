plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.rafsan.newsapp.core"
    compileSdk = Deps.Versions.compile_sdk

    defaultConfig {
        minSdk = Deps.Versions.min_sdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        buildConfig = true
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    api(project(":domain"))

    implementation(Deps.AndroidX.ktx_core)
    implementation(Deps.Coroutines.core)
    implementation(Deps.Coroutines.android)

    // Hilt
    implementation(Deps.Hilt.android)
    kapt(Deps.Hilt.android_compiler)

    // Retrofit & OkHttp
    implementation(Deps.Retrofit.main)
    implementation(Deps.Retrofit.converterGSON)
    implementation(Deps.OkHttp.main)
    implementation(Deps.OkHttp.logging_interceptor)

    // Room
    implementation(Deps.Room.runtime)
    implementation(Deps.Room.ktx)
    implementation(Deps.Room.paging)
    kapt(Deps.Room.compiler)

    // Paging
    implementation(Deps.Paging.runtime)

    testImplementation(Deps.junit)
    androidTestImplementation(Deps.AndroidX.Test.junit)
}