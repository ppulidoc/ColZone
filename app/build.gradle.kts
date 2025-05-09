plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    //PLUGIN PER AL KSP
    id("com.google.devtools.ksp") version "1.9.20-1.0.14"
    id("com.google.gms.google-services")
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
    implementation("com.google.firebase:firebase-storage:20.3.0")

    //DEPENDENCIES A MÀ PER A ROOM
    implementation("androidx.room:room-runtime:2.6.1")
   /* implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)
    ksp("androidx.room:room-compiler:2.6.1")*/
    implementation("androidx.room:room-ktx:2.6.1")
    implementation(libs.androidx.navigation.fragment.ktx)
    implementation(libs.androidx.navigation.ui.ktx)

    /* GLIDE */

    // Glide (con KSP)
    implementation("com.github.bumptech.glide:glide:4.15.1")
    implementation(libs.firebase.storage.ktx)
    ksp("com.github.bumptech.glide:ksp:4.15.1")
    annotationProcessor("com.github.bumptech.glide:compiler:4.15.1")


    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
