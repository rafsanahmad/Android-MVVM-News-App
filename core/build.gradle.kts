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

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures { buildConfig = false }
}

dependencies {
    implementation(Deps.AndroidX.ktx_core)
    implementation(Deps.AndroidX.appCompat)
    implementation(Deps.Google.material)
    implementation(Deps.Retrofit.main)
    implementation(Deps.Timber.timber)
    implementation(Deps.Coroutines.core)
    implementation(Deps.Hilt.android)
    kapt(Deps.Hilt.android_compiler)
}
