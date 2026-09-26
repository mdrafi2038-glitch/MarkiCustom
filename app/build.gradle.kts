plugins {
    id("com.android.application")
}

android {
    namespace = "com.markicustom.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.markicustom.app"
        minSdk = 23
        targetSdk = 35
        versionCode = 2
        versionName = "2.0"
    }

    java {
        toolchain.languageVersion.set(JavaLanguageVersion.of(17))
    }
}
