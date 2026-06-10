plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.compose")
    id("com.google.devtools.ksp")
    id("com.google.dagger.hilt.android")
}

configurations.all {
    resolutionStrategy {
        force("androidx.compose.material:material-ripple:1.7.6")
        force("androidx.compose.material:material:1.7.6")
        force("androidx.compose.foundation:foundation:1.7.6")
        force("androidx.compose.foundation:foundation-layout:1.7.6")
        force("androidx.compose.ui:ui:1.7.6")
        force("androidx.compose.ui:ui-graphics:1.7.6")
        force("androidx.compose.ui:ui-text:1.7.6")
        force("androidx.compose.ui:ui-tooling:1.7.6")
        force("androidx.compose.ui:ui-tooling-preview:1.7.6")
        force("androidx.compose.runtime:runtime:1.7.6")
        force("androidx.compose.material3:material3:1.3.1")
    }
}

android {
    namespace = "com.kiriplatform.app"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.kiriplatform.app"
        minSdk = 26
        targetSdk = 35
        versionCode = 29
        versionName = "1.29"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    signingConfigs {
        create("release") {
            storeFile = file("release.keystore")
            storePassword = "asgpass"
            keyAlias = "asg_alias"
            keyPassword = "asgpass"
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
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
        compose = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    lint {
        abortOnError = false
        checkReleaseBuilds = false
        disable += "LintError"
        disable += "NewApi"
    }
    tasks.whenTaskAdded {
        if (name == "lintVitalRelease") {
            enabled = false
        }
    }
}

dependencies {
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.2")
    implementation("androidx.activity:activity-compose:1.8.1")
    implementation(platform("androidx.compose:compose-bom:2024.12.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.material:material-ripple")
    implementation("androidx.compose.ui:ui-text-google-fonts")
    implementation("androidx.compose.material:material-icons-extended")

    // Navigation
    implementation("androidx.navigation:navigation-compose:2.7.7")
    
    // Hilt
    implementation("com.google.dagger:hilt-android:2.55")
    ksp("com.google.dagger:hilt-compiler:2.55")
    implementation("androidx.hilt:hilt-navigation-compose:1.2.0")
    
    // Retrofit & Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")
    
    // Room
    implementation("androidx.room:room-runtime:2.7.0")
    implementation("androidx.room:room-ktx:2.7.0")
    ksp("androidx.room:room-compiler:2.7.0")
    
    // Image loading
    implementation("io.coil-kt:coil-compose:2.5.0")

    // Maps
    implementation("com.google.maps.android:maps-compose:4.3.0")
    implementation("com.google.android.gms:play-services-maps:18.2.0")

    // Accompanist (for flow rows, permissions)
    implementation("com.google.accompanist:accompanist-permissions:0.34.0")
    implementation("com.google.accompanist:accompanist-flowlayout:0.34.0")

    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.12.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    // Socket.io for Real-time
    implementation("io.socket:socket.io-client:2.1.0")

    // WorkManager for Offline Synchronization
    implementation("androidx.work:work-runtime-ktx:2.9.0")

    // Premium UI Effects - using native Compose only for Play Store compliance
}
