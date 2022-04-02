object Versions {
    val versionName = "7.0.15" // X.Y.Z; X = Major, Y = minor, Z = Patch level
    private val versionCodeBase = 70150 // XYYZZM; M = Module (tv, mobile)
    val versionCodeMobile = versionCodeBase + 3

    const val COMPILE_SDK = 30
    const val TARGET_SDK = 30
    const val MIN_SDK = 21

    const val ANDROID_GRADLE_PLUGIN = "7.0.0-beta05"
    const val BENCHMARK = "1.0.0"
    const val COMPOSE = "1.0.0-beta04"
    const val FIREBASE_CRASHLYTICS = "2.3.0"
    const val GOOGLE_SERVICES = "4.3.3"
    const val KOTLIN = "1.4.32"
    const val NAVIGATION = "2.3.5"
    const val HILT_AGP = "2.36"

    // TODO: Remove this once the version for
    //  "org.threeten:threetenbp:${Versions.threetenbp}:no-tzdb" using java-platform in the
    //  depconstraints/build.gradle.kts is defined
    const val THREETENBP = "1.3.6"
}
