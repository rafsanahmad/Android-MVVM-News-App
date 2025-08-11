plugins {
    kotlin("jvm")
}

dependencies {
    implementation(Deps.Coroutines.core)
    testImplementation(Deps.junit)
    testImplementation(Deps.Coroutines.test)
}