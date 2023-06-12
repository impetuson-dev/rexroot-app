@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
    id("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.impetuson.rexroot"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.impetuson.rexroot"
        minSdk = 23
        targetSdk = 33
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        dataBinding = true
    }
}

dependencies {

    implementation(libs.core.ktx)
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.navigation.fragment)
    implementation(libs.firebase.auth.ktx)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.test.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Firestore
    implementation("com.google.firebase:firebase-firestore-ktx:24.6.1")

    // Livedata
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.3.1")

    // ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx: 2.3.1")
    implementation("androidx.lifecycle:lifecycle-viewmodel-savedstate: 2.3.1")

    // Advanced Navigation - Jetpack Compose
    implementation("androidx.navigation:navigation-fragment-ktx:2.5.3")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.3")

    implementation("com.wdullaer:materialdatetimepicker:3.6.4")

    // LottieFiles
    implementation("com.airbnb.android:lottie:6.0.1")
}