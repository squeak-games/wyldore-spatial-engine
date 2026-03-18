plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
}

android {
    namespace = "com.squeakgames.wyldore"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.squeakgames.wyldore"
        minSdk = 31
        targetSdk = 35
        versionCode = 1
        versionName = "0.1.0-prototype"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
        create("prototype") {
            initWith(getByName("debug"))
            applicationIdSuffix = ".prototype"
            isDebuggable = true
            matchingFallbacks += listOf("debug")
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions { jvmTarget = "17" }

    buildFeatures { compose = true }
}

dependencies {
    implementation(project(":engine"))
    implementation(project(":spatial-audio"))

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.activity.compose)

    // Compose for XR is the declarative layer the deck refers to as the
    // "Compose for XR Layer" — a thin, glanceable, high-contrast HUD.
    implementation(platform("androidx.compose:compose-bom:${libs.versions.composeBom.get()}"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation(libs.androidx.xr.scenecore)
    implementation(libs.androidx.xr.compose)

    // Ambient biometric data loop (slide 2 of the catalyst deck).
    implementation(libs.androidx.health.connect)

    // Geofencing — sanctuary zone detection (local-only, no network).
    implementation(libs.play.services.location)

    testImplementation(libs.junit)
    testImplementation(libs.coroutines.test)
    testImplementation(libs.robolectric)
}