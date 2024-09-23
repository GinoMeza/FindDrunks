plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.gin.screenbeody"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.gin.screenbeody"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    implementation(platform("com.google.firebase:firebase-bom:33.2.0"))
    implementation("com.google.firebase:firebase-analytics")
    //modelo de machine learning
    implementation ("com.google.firebase:firebase-ml-modeldownloader:24.0.3")
    // TensorFlow Lite para Android
    implementation ("org.tensorflow:tensorflow-lite:2.12.0")
    // Delegado de GPU (opcional si piensas usar la GPU)
    implementation ("org.tensorflow:tensorflow-lite-gpu:2.12.0")
    // Selectores de modelos de TensorFlow Lite (opcional)
    implementation ("org.tensorflow:tensorflow-lite-select-tf-ops:2.12.0")
}