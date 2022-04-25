plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
//    id("dagger.hilt.android.plugin")
}

android {

    compileSdk = Versions.COMPILE_SDK
    defaultConfig {

        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK

        buildConfigField(
            "String",
            "BOOTSTRAP_CONF_DATA_FILENAME", project.properties["bootstrap_conference_data_filename"] as String
        )

    }
    // To avoid the compile error: "Cannot inline bytecode built with JVM target 1.8
    // into bytecode that is being built with JVM target 1.6"
    kotlinOptions {
        val options = this as org.jetbrains.kotlin.gradle.dsl.KotlinJvmOptions
        options.jvmTarget = "1.8"
    }
}

dependencies {

    api(platform(project(":depconstraints")))
    kapt(platform(project(":depconstraints")))
    androidTestApi(platform(project(":depconstraints")))

    implementation(project(":model"))
    implementation(project(":shared"))

    // Architecture Components
    implementation(Libs.ROOM_KTX)
    implementation(Libs.ROOM_RUNTIME)
    kapt(Libs.ROOM_COMPILER)

    // Utils
    api(Libs.TIMBER)
    implementation(Libs.GSON)

    // DataStore
    implementation(Libs.DATA_STORE_PREFERENCES)

    // Kotlin
    implementation(Libs.KOTLIN_STDLIB)

    // Coroutines
    api(Libs.COROUTINES)

    // Dagger Hilt
    implementation(Libs.HILT_ANDROID)
    kapt(Libs.HILT_COMPILER)

    // Firebase
    api(Libs.FIREBASE_AUTH)

}