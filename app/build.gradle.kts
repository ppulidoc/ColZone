plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //PLUGIN PER AL KSP
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
}


android {
    namespace = "com.paudam.colzone"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.paudam.colzone"
        minSdk = 26
        targetSdk = 35
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
    kotlinOptions {
        jvmTarget = "11"
    }
    dataBinding {
        enable = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    //implementation(libs.androidx.room.common)

    implementation("com.google.firebase:firebase-auth:22.3.1")
    implementation("com.google.firebase:firebase-analytics:21.5.1")
    implementation("com.google.firebase:firebase-firestore:25.1.2")

    //DEPENDENCIES A MÀ PER A ROOM
    implementation("androidx.room:room-runtime:2.6.1")
   /* implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    ksp("androidx.room:room-compiler:2.6.1")*/
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)



    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}