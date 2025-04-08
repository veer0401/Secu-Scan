plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.secuscan2"
    compileSdk = 35 // Updated to 34

    defaultConfig {
        applicationId = "com.example.secuscan2"
        minSdk = 26
        targetSdk = 35 // Updated to 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation(libs.material) // Updated Material Components
    implementation(libs.navigation.fragment) // Updated Navigation
    implementation(libs.navigation.ui) // Updated Navigation
    implementation(libs.appcompat) // Updated AppCompat
    implementation(libs.activity) // Updated Activity
    implementation(libs.constraintlayout) // Updated ConstraintLayout
    implementation(libs.fragment) // Updated Fragment
    implementation(libs.core) // Updated Core
    implementation(libs.android.materialfilepicker)

    // Unit testing dependencies
    testImplementation(libs.junit) // Updated JUnit
    androidTestImplementation(libs.ext.junit) // Updated AndroidX Test
    androidTestImplementation(libs.espresso.core) // Updated Espresso
}

// Remove forcing the older version of Material Components
configurations.all {
    resolutionStrategy {
        // No need to force the older versions now
    }
}
