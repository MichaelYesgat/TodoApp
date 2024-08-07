plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "edu.metrostate.todoapp"
    compileSdk = 34

    defaultConfig {
        applicationId = "edu.metrostate.todoapp"
        minSdk = 21
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}



dependencies {
    // Core and Kotlin libraries
    implementation(libs.androidx.core.ktx)
    implementation(libs.kotlin.reflect)

    // Lifecycle libraries
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)


    // Compose libraries
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.material.icons.extended.v151)

    // Moshi and Retrofit libraries
    implementation(libs.moshi.v1130)
    implementation(libs.moshi.kotlin.v1130)
    implementation(libs.retrofit.v290)
    implementation(libs.converter.moshi.v290)

    // OkHttp and Logging Interceptor
    implementation(libs.okhttp.v493)
    implementation(libs.logging.interceptor.v493)

    // Navigation libraries
    implementation(libs.androidx.navigation.runtime.ktx)
    implementation(libs.androidx.navigation.compose)

    // Additional libraries
    implementation(libs.androidx.contentpager)
    implementation(libs.androidx.tracing.perfetto.handshake)
    implementation(libs.androidx.work.runtime.ktx)
    implementation(libs.converter.gson)
    implementation(libs.volley)
    implementation(libs.androidx.constraintlayout.compose.android)

    // Testing libraries
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}
