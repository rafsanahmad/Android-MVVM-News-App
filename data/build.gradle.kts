plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.rafsan.newsapp.data"
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
    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Project Dependencies
    implementation(project(":core"))
    implementation(project(":domain"))

    // Hilt
    implementation(Deps.Hilt.android)
    kapt(Deps.Hilt.android_compiler)
    kapt(Deps.Hilt.compiler) // For HiltViewModel

    // Room
    implementation(Deps.AndroidX.Room.runtime)
    implementation(Deps.AndroidX.Room.ktx)
    kapt(Deps.AndroidX.Room.compiler)
    implementation(Deps.AndroidX.Room.paging) // For Room Paging 3 integration

    // Paging
    implementation(Deps.AndroidX.Paging.runtime)

    // Retrofit & OkHttp
    implementation(Deps.Retrofit.main)
    implementation(Deps.Retrofit.converterGSON)
    implementation(Deps.OkHttp.main)
    implementation(Deps.OkHttp.logging_interceptor)

    // Coroutines
    implementation(Deps.Coroutines.core)
    implementation(Deps.Coroutines.android)

    // Timber
    implementation(Deps.Timber.timber)

    // Testing
    testImplementation(Deps.Test.junit)
    testImplementation(Deps.Test.truth)
    testImplementation(Deps.Test.MockK.mockk)
    testImplementation(Deps.Coroutines.test)
    testImplementation(Deps.AndroidX.arch_core_testing)

    androidTestImplementation(Deps.AndroidX.Test.junit)
    androidTestImplementation(Deps.AndroidX.Test.espresso_core)
    androidTestImplementation(Deps.AndroidX.Test.runner)
    androidTestImplementation(Deps.Test.MockK.mockk_android)
    androidTestImplementation(Deps.Hilt.android_testing) // For Hilt testing
    kaptAndroidTest(Deps.Hilt.android_compiler) // For Hilt testing
}
