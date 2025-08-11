plugins {
    id("com.android.library")
    kotlin("android")
}

android {
    namespace = "com.rafsan.newsapp.domain"
    compileSdk = Deps.Versions.compile_sdk

    defaultConfig {
        minSdk = Deps.Versions.min_sdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
    implementation(Deps.Coroutines.core)
    implementation(Deps.Paging.runtime)
    testImplementation(Deps.junit)
    testImplementation(Deps.Coroutines.test)
    testImplementation(Deps.Test.truth)
}