plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}

android {
    namespace = "com.rafsan.newsapp.feature.news"
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
    implementation(project(":data"))

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

    testImplementation(Deps.junit)
    testImplementation(Deps.Coroutines.test)
    testImplementation(Deps.Test.truth)

    androidTestImplementation(platform(Deps.Compose.bom))
    androidTestImplementation(Deps.Compose.uiTestJunit4)
    debugImplementation(Deps.Compose.uiTooling)
    debugImplementation(Deps.Compose.uiTestManifest)
}