import java.io.FileInputStream
import java.util.Properties

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
    buildFeatures { buildConfig = true }

    // Read API key from local.properties
    val localProperties = Properties()
    val localPropertiesFile = rootProject.file("local.properties") // Use rootProject.file
    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use { fis ->
            localProperties.load(fis)
        }
    }
    // Define WEATHER_API_KEY in BuildConfig. Fallback to a placeholder if not found.
    val apiKey = localProperties.getProperty(
        "NEWS_API_KEY",
        "YOUR_API_KEY_HERE_IF_NOT_IN_LOCAL_PROPERTIES"
    )

    buildTypes.all {
        buildConfigField("String", "NEWS_API_KEY", "\"${apiKey}\"")
        buildConfigField("String", "BASE_URL", "\"https://newsapi.org/\"")
    }
}

dependencies {
    // Project Dependencies
    implementation(project(":core"))
    implementation(project(":domain"))

    // Hilt
    implementation(Deps.Hilt.android)
    kapt(Deps.Hilt.android_compiler)

    // Room
    implementation(Deps.Room.runtime)
    implementation(Deps.Room.ktx)
    kapt(Deps.Room.compiler)
    implementation(Deps.Room.paging) // For Room Paging 3 integration

    // Paging
    implementation(Deps.Paging.runtime)

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

    androidTestImplementation(Deps.Test.junit)
    androidTestImplementation(Deps.Test.espresso_core)
    androidTestImplementation(Deps.Test.runner)
    androidTestImplementation(Deps.Test.MockK.mockk_android)
    androidTestImplementation(Deps.Hilt.android_testing)
    kaptAndroidTest(Deps.Hilt.android_compiler)
}
