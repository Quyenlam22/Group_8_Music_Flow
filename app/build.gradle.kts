import java.io.FileInputStream
import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
//    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}

// *** ĐỌC CẤU HÌNH KÝ TỪ local.properties ***
// Đảm bảo file local.properties chứa: storeFile, storePassword, keyAlias, keyPassword
val signingPropsFile = rootProject.file("local.properties")
val signingProperties = Properties()
if (signingPropsFile.exists()) {
    // SỬA LỖI: Bỏ từ khóa new
    signingProperties.load(FileInputStream(signingPropsFile))
}
// **********************************************

android {
    namespace = "com.vn.btl"
    compileSdk = 36

    defaultConfig {
        applicationId = "com.vn.btl"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    // 🔑 KHỐI THÊM MỚI 1/2: Cấu hình Khóa Phát hành (Release Key)
    signingConfigs {
        // Cấu hình ký chung cho Khóa Phát hành của dự án
        create("releaseConfig") { // Dùng create("tên_cấu_hình") trong Kotlin DSL
            // SỬA LỖI: Dùng toán tử index [] và cú pháp Kotlin
            if (signingProperties.containsKey("storeFile")) {
                // SỬA LỖI: Sử dụng toán tử non-null assertion (!!) và ép kiểu sang String (as String)
                storeFile = file(signingProperties["storeFile"] as String) // Dùng 'as String'
                storePassword = signingProperties["storePassword"] as String // Dùng 'as String'
                keyAlias = signingProperties["keyAlias"] as String // Dùng 'as String'
                keyPassword = signingProperties["keyPassword"] as String // Dùng 'as String'
            } else {
                // Nếu không có cấu hình, có thể để trống
            }
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            // 🔑 KHỐI THÊM MỚI 2/2: Áp dụng Keystore Phát hành cho bản Release
            signingConfig = signingConfigs.getByName("releaseConfig")
        }

        debug {
            // Áp dụng Keystore Phát hành cho bản Debug để test Google Sign-in
            signingConfig = signingConfigs.getByName("releaseConfig")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:33.3.0"))
    implementation("com.google.firebase:firebase-auth")
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-storage")

    // 🔑 NEW: Google Sign-In Library (Necessary for mGoogleSignInClient)
    implementation("com.google.android.gms:play-services-auth:21.0.0")
    implementation("com.facebook.android:facebook-login:latest.release")

    // AndroidX / UI
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)

    // Test
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.viewpager2:viewpager2:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    // 🌐 Retrofit + Gson
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // 🖼️ Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")
    annotationProcessor("com.github.bumptech.glide:compiler:4.16.0")

    // 💾 Room
    val room_version = "2.8.3"
    implementation("androidx.room:room-runtime:$room_version")
    annotationProcessor("androidx.room:room-compiler:$room_version")
}