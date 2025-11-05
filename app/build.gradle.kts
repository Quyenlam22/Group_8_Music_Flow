plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.vn.btl"
    compileSdk = 34  // Giảm xuống 34 cho ổn định

    defaultConfig {
        applicationId = "com.vn.btl"
        minSdk = 24
        targetSdk = 34  // Giảm xuống 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    // ANDROIDX CORE
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.activity:activity-ktx:1.8.0")

    // UI
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // MEDIA PLAYBACK
    implementation("com.google.android.exoplayer:exoplayer:2.19.1")

    // IMAGE LOADING
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // JSON PARSING
    implementation("com.google.code.gson:gson:2.10.1")

    // TESTING
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")





}