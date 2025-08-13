plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.rafsan.newsapp.feature.search"
    compileSdk = Deps.Versions.compile_sdk

    defaultConfig {
        minSdk = Deps.Versions.min_sdk
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
    }

    buildFeatures {
        compose = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = Deps.Versions.composeCompiler
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
    implementation(project(":core"))
    implementation(project(":domain"))

    implementation(platform(Deps.Compose.bom))
    implementation(Deps.Compose.ui)
    implementation(Deps.Compose.uiGraphics)
    implementation(Deps.Compose.uiToolingPreview)
    implementation(Deps.Compose.material3)
    implementation(Deps.AndroidX.activity_compose)
    implementation(Deps.Navigation.navigationCompose)
    implementation(Deps.Navigation.hiltCompose)

    implementation(Deps.Paging.runtime)
    implementation(Deps.Paging.compose)

    implementation(Deps.Coil.compose)

    implementation(Deps.Coroutines.core)
    implementation(Deps.Coroutines.android)

    implementation(Deps.Lifecycle.viewmodel)
    implementation(Deps.Lifecycle.lifeCycleRunTime)

    // Hilt
    implementation(Deps.Hilt.android)
    kapt(Deps.Hilt.android_compiler)

    // Timber
    implementation(Deps.Timber.timber)

    // Unit Test Implementations
    testImplementation(Deps.junit)
    testImplementation(Deps.Coroutines.test)
    testImplementation(Deps.Test.truth)
    testImplementation(Deps.AndroidX.arch_core_testing)
    testImplementation(Deps.Test.MockK.mockk) // For SearchViewModelTest
    testImplementation(Deps.Test.turbine)

    // Android Instrumented UI Test Implementations
    androidTestImplementation(platform(Deps.Compose.bom))
    androidTestImplementation(Deps.Compose.uiTestJunit4)
    androidTestImplementation(Deps.AndroidX.Test.junit) 
    androidTestImplementation(Deps.AndroidX.Test.espresso_core)
    androidTestImplementation(Deps.Hilt.testing)
    kaptAndroidTest(Deps.Hilt.android_compiler)
    androidTestImplementation(Deps.Coroutines.test)
    androidTestImplementation(Deps.Navigation.testing)
    androidTestImplementation(Deps.Test.MockK.mockk) // MockK for UI tests (SearchScreenTest)

    debugImplementation(Deps.Compose.uiTooling)
    debugImplementation(Deps.Compose.uiTestManifest)
}
