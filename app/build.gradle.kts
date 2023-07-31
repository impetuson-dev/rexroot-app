@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("org.jlleitschuh.gradle.ktlint") version "10.2.0"
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    buildToolsVersion = "33"
    namespace = "com.impetuson.rexroot"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.impetuson.rexroot"
        minSdk = 26
        targetSdk = 33
        versionCode = 4   // Have to be incremented by 1 for each prod release
        versionName = "@string/versionName"  // Have to be changed for each prod release

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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        dataBinding = true
    }

    kotlin {
        jvmToolchain(17)
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.firebase.auth.ktx)
    implementation(libs.firebase.database.ktx)
    testImplementation(libs.junit)
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)
    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.1.1"))

    // Firestore
    implementation("com.google.firebase:firebase-firestore-ktx")

    // Firebase Storage
    implementation("com.google.firebase:firebase-storage-ktx")

    implementation("com.squareup.okhttp3:okhttp:3.9.0")

    // Livedata
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.4.1")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx: 2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate: 2.3.1")

    // Advanced Navigation - Jetpack Compose
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    implementation("com.wdullaer:materialdatetimepicker:3.6.4")

    // LottieFiles
    implementation("com.airbnb.android:lottie:6.0.1")

    // Gson
    implementation("com.google.code.gson:gson:2.8.9")

    // Material
    implementation("com.google.android.material:material:1.4.0")

    // ViewPager2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}