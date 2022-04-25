plugins {
    id("com.android.library")
    kotlin("android")
    kotlin("kapt")
    id("dagger.hilt.android.plugin")
}


android {
    compileSdk = Versions.COMPILE_SDK
    defaultConfig {

        minSdk = Versions.MIN_SDK
        targetSdk = Versions.TARGET_SDK

        buildConfigField("String", "CONFERENCE_TIMEZONE", project.properties["conference_timezone"] as String)
        buildConfigField("String", "CONFERENCE_DAY1_START", project.properties["conference_day1_start"] as String)
        buildConfigField("String", "CONFERENCE_DAY1_END", project.properties["conference_day1_end"] as String)
        buildConfigField("String", "CONFERENCE_DAY2_START", project.properties["conference_day2_start"] as String)
        buildConfigField("String", "CONFERENCE_DAY2_END", project.properties["conference_day2_end"] as String)
        buildConfigField("String", "CONFERENCE_DAY3_START", project.properties["conference_day3_start"] as String)
        buildConfigField("String", "CONFERENCE_DAY3_END", project.properties["conference_day3_end"] as String)

        buildConfigField("String", "CONFERENCE_DAY1_AFTERHOURS_START", project.properties["conference_day1_afterhours_start"] as String)
        buildConfigField("String", "CONFERENCE_DAY2_CONCERT_START", project.properties["conference_day2_concert_start"] as String)

        buildConfigField(
            "String",
            "CONFERENCE_WIFI_OFFERING_START", project.properties["conference_wifi_offering_start"] as String
        )
    }

    buildTypes {

    }

    // Some libs (such as androidx.core:core-ktx 1.2.0 and newer) require Java 8
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
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

    // Architecture Components
    implementation(Libs.LIFECYCLE_LIVE_DATA_KTX)
    implementation(Libs.LIFECYCLE_RUNTIME_KTX)
    kapt(Libs.LIFECYCLE_COMPILER)

    // Kotlin
    implementation(Libs.KOTLIN_STDLIB)

    // Dagger Hilt
    implementation(Libs.HILT_ANDROID)
    androidTestImplementation(Libs.HILT_TESTING)
    kapt(Libs.HILT_COMPILER)
    kaptAndroidTest(Libs.HILT_COMPILER)

    // Utils
    api(Libs.TIMBER)

    // Firebase
    api(Libs.FIREBASE_CONFIG)
    api(Libs.FIREBASE_ANALYTICS)


    testImplementation("junit:junit:4.+")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")

}
