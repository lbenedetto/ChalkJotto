plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.google.services)
    alias(libs.plugins.firebase.crashlytics)
    alias(libs.plugins.kotlin.ksp)
}

android {
    signingConfigs {
        create("release") {
            //Define these in USER_HOME/.gradle/gradle.properties
            keyAlias = providers.gradleProperty("JOTTO_RELEASE_KEY_ALIAS").get()
            keyPassword = providers.gradleProperty("JOTTO_RELEASE_KEY_PASSWORD").get()
            storeFile = file(providers.gradleProperty("JOTTO_RELEASE_STORE_FILE").get())
            storePassword = providers.gradleProperty("JOTTO_RELEASE_STORE_PASSWORD").get()
        }
        getByName("debug") {
            storeFile = file(providers.gradleProperty("JOTTO_DEBUG_STORE_FILE").get())
        }
    }

    buildFeatures {
        viewBinding = true
        buildConfig = true
    }

    compileSdk = 36
    defaultConfig {
        applicationId = "com.benedetto.chalkjotto"
        minSdk = 23
        targetSdk = 36
        versionCode = 15
        versionName = "25.10.0"
        signingConfig = signingConfigs.getByName("release")
    }
    buildTypes {
        release {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    namespace = "com.benedetto.chalkjotto"
    lint {
        disable += "ClickableViewAccessibility"
    }
}

kotlin {
    jvmToolchain(21)
}

dependencies {
    implementation(fileTree(mapOf("include" to listOf("*.jar"), "dir" to "libs")))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.coordinatorlayout)
    implementation(libs.firebase.crashlytics)
    implementation(libs.kotlinx.serialization.json)
    implementation(libs.androidx.recyclerview)

    // This was added as a workaround to fix a bug with kotlinx.serialization
    // Try removing it and seeing if GameState gets a bunch of false errors highlighted
    implementation(libs.androidx.annotation.experimental)

    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    ksp(libs.androidx.room.compiler)
    implementation(libs.vico.views)
    implementation(libs.androidx.lifecycle.viewmodel.ktx)
    implementation(libs.androidx.fragment.ktx)
}