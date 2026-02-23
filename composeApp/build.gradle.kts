plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.google.services)
    alias(libs.plugins.ksp)
}

kotlin {
    // Explicit androidTarget declaration - must be exactly like this
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "17"
            }
        }
    }
    
    sourceSets {
        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material3)
            implementation(compose.materialIconsExtended)
            implementation(compose.ui)
            implementation(compose.components.resources)
            implementation(libs.compose.uiToolingPreview)
            implementation(libs.kmp.notifier)
            implementation(libs.kotlinx.datetime)
            implementation(libs.kotlinx.coroutines.core)
        }
        
        androidMain.dependencies {
            implementation(libs.androidx.core.ktx)
            implementation(libs.androidx.appcompat)
            implementation(libs.androidx.activity.compose)
            
            // AndroidX Lifecycle for Compose
            implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0")
            implementation("androidx.lifecycle:lifecycle-runtime-compose:2.7.0")
            
            // Firebase - without platform() to avoid deprecation
            implementation("com.google.firebase:firebase-bom:33.0.0")
            implementation(libs.firebase.messaging)
            
            implementation(libs.androidx.room.runtime)
            implementation(libs.androidx.room.ktx)
            implementation(libs.kotlinx.coroutines.android)
        }
        
        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }
    }
}

android {
    namespace = "com.turjaun.serverstatuscookies"
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    defaultConfig {
        applicationId = "com.turjaun.serverstatuscookies"
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = 1
        versionName = "1.0"
    }
    
    buildFeatures {
        compose = true
    }
    
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.8"
    }
    
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    
    buildTypes {
        getByName("release") {
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
}

// KSP configuration for Room
ksp {
    arg("room.schemaLocation", "$projectDir/schemas")
}

dependencies {
    debugImplementation(libs.compose.uiTooling)
    // Use regular ksp, not kspAndroid
    ksp(libs.androidx.room.compiler)
}