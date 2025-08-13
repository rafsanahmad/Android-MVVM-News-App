plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
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
    // Project Dependencies
    implementation(project(":core"))

    // Hilt
    implementation(Deps.Hilt.android)
    kapt(Deps.Hilt.android_compiler)
    kapt(Deps.Hilt.compiler) // For HiltViewModel if any UseCase is a ViewModel (uncommon)

    // Coroutines
    implementation(Deps.Coroutines.core)

    // Paging (Common, as repository interfaces and UseCases might deal with PagingData)
    implementation(Deps.AndroidX.Paging.common)

    // Testing
    testImplementation(Deps.Test.junit)
    testImplementation(Deps.Test.truth)
    testImplementation(Deps.Test.MockK.mockk)
    testImplementation(Deps.Coroutines.test)
}
