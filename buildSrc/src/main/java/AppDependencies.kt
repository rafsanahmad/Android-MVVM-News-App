/*
 * *
 *  * Created by Rafsan Ahmad on 9/27/21, 5:30 PM
 *  * Copyright (c) 2021 . All rights reserved.
 *
 */

object Deps {
    object Versions {
        const val compile_sdk = 34
        const val min_sdk = 21
        const val target_sdk = 34
        const val app_version_code = 1
        const val app_version_name = "1.0"
        const val gradle_plugin = "8.5.2"
        const val kotlinVersion = "1.9.24"
        const val coroutinesVersion = "1.8.1"
        const val junit = "4.13.2"
        const val material = "1.12.0"
        const val android_test = "1.6.1"
        const val espresso = "3.6.1"
        const val android_test_junit = "1.2.1"
        const val arch_core_testing = "2.2.0"
        const val ktx_core = "1.13.1"
        const val ktx_activity = "1.9.2"
        const val appCompatVersion = "1.7.0"
        const val roomVersion = "2.6.1"
        const val hiltVersion = "2.51.1"
        const val retrofit = "2.11.0"
        const val okhttp = "4.12.0"
        const val mockwebServer = "4.12.0"
        const val truth = "1.4.2"
        const val robolectric = "4.12.2"
        const val navigationVersion = "2.7.7"
        const val pagingVersion = "3.3.2"
        const val composeBom = "2024.06.00"
        const val composeCompiler = "1.5.14"
        const val coilVersion = "2.6.0"
    }

    const val gradle_plugin = "com.android.tools.build:gradle:${Versions.gradle_plugin}"

    const val junit = "junit:junit:${Versions.junit}"

    object Google {
        const val material = "com.google.android.material:material:${Versions.material}"
    }

    object Kotlin {
        const val gradle_plugin =
            "org.jetbrains.kotlin:kotlin-gradle-plugin:${Versions.kotlinVersion}"
    }

    object Coroutines {
        const val core =
            "org.jetbrains.kotlinx:kotlinx-coroutines-core:${Versions.coroutinesVersion}"
        const val android =
            "org.jetbrains.kotlinx:kotlinx-coroutines-android:${Versions.coroutinesVersion}"
        const val test =
            "org.jetbrains.kotlinx:kotlinx-coroutines-test:${Versions.coroutinesVersion}"
    }

    object AndroidX {
        const val appCompat = "androidx.appcompat:appcompat:${Versions.appCompatVersion}"
        const val ktx_core = "androidx.core:core-ktx:${Versions.ktx_core}"
        const val ktx_activity = "androidx.activity:activity-ktx:${Versions.ktx_activity}"
        const val activity_compose = "androidx.activity:activity-compose:${Versions.ktx_activity}"

        object Test {
            const val core = "androidx.test:core:${Versions.android_test}"
            const val rules = "androidx.test:rules:${Versions.android_test}"
            const val runner = "androidx.test:runner:${Versions.android_test}"
            const val junit = "androidx.test.ext:junit:${Versions.android_test_junit}"
            const val junitKtx = "androidx.test.ext:junit-ktx:${Versions.android_test_junit}"
            const val coreKtx = "androidx.test:core-ktx:${Versions.android_test}"
        }
    }

    object Lifecycle {
        const val viewmodel = "androidx.lifecycle:lifecycle-viewmodel-ktx:2.8.4"
        const val lifeCycleLiveData = "androidx.lifecycle:lifecycle-livedata-ktx:2.8.4"
        const val lifeCycleRunTime = "androidx.lifecycle:lifecycle-runtime-ktx:2.8.4"
    }

    object Room {
        const val runtime = "androidx.room:room-runtime:${Versions.roomVersion}"
        const val compiler = "androidx.room:room-compiler:${Versions.roomVersion}"
        const val ktx = "androidx.room:room-ktx:${Versions.roomVersion}"
        const val paging = "androidx.room:room-paging:${Versions.roomVersion}"
        const val testing = "androidx.room:room-testing:${Versions.roomVersion}"
    }

    object Navigation {
        const val navigationFragment =
            "androidx.navigation:navigation-fragment-ktx:${Versions.navigationVersion}"
        const val navigationKtx =
            "androidx.navigation:navigation-ui-ktx:${Versions.navigationVersion}"
        const val navigationCompose = "androidx.navigation:navigation-compose:${Versions.navigationVersion}"
    }

    object Hilt {
        const val android = "com.google.dagger:hilt-android:${Versions.hiltVersion}"
        const val android_compiler =
            "com.google.dagger:hilt-android-compiler:${Versions.hiltVersion}"
        const val gradlePlugin =
            "com.google.dagger:hilt-android-gradle-plugin:${Versions.hiltVersion}"
    }

    object Retrofit {
        const val main = "com.squareup.retrofit2:retrofit:${Versions.retrofit}"
        const val converterGSON = "com.squareup.retrofit2:converter-gson:${Versions.retrofit}"
    }

    object OkHttp {
        const val main = "com.squareup.okhttp3:okhttp:${Versions.okhttp}"
        const val logging_interceptor =
            "com.squareup.okhttp3:logging-interceptor:${Versions.okhttp}"
        const val mockWebServer = "com.squareup.okhttp3:mockwebserver:${Versions.mockwebServer}"
        }
 
     object Paging {
         const val runtime = "androidx.paging:paging-runtime:${Versions.pagingVersion}"
         const val compose = "androidx.paging:paging-compose:${Versions.pagingVersion}"
     }
 
     object Timber {
         const val timber = "com.jakewharton.timber:timber:5.0.1"
     }
 
     object Compose {
const val bom = "androidx.compose:compose-bom:${Versions.composeBom}"
        const val ui = "androidx.compose.ui:ui"
        const val uiGraphics = "androidx.compose.ui:ui-graphics"
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val material3 = "androidx.compose.material3:material3"
        const val runtime = "androidx.compose.runtime:runtime"
        const val runtimeLivedata = "androidx.compose.runtime:runtime-livedata"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest"
    }

    object Coil {
        const val compose = "io.coil-kt:coil-compose:${Versions.coilVersion}"
    }

    object Test {
        object Mockito {
            const val core = "org.mockito:mockito-core:5.12.0"
            const val inline = "org.mockito:mockito-inline:5.2.0"
            const val kotlin = "org.mockito.kotlin:mockito-kotlin:5.3.1"
        }
        const val truth = "com.google.truth:truth:${Versions.truth}"
        const val robolectric = "org.robolectric:robolectric:${Versions.robolectric}"
    }
}
