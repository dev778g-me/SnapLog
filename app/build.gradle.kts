import org.jetbrains.kotlin.konan.properties.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("com.google.devtools.ksp")
    alias(libs.plugins.kotlin.serialization)
    id("kotlin-parcelize")
 //   kotlin("plugin.serialization") version "1.9.0"

}

android {
    namespace = "com.dev.snaplog"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.dev.snaplog"
        minSdk = 27
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

       val properties = Properties()
       properties.load(project.rootProject.file("local.properties").inputStream() )
        buildConfigField("String", "API_KEY", properties.getProperty("API_KEY"))
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
    buildFeatures {
        compose = true
        buildConfig = true
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    //ML kit for text recognition
    implementation (libs.text.recognition)
    //ML kit for Image recognition
    implementation (libs.object1.detection)
    //Coil
    implementation(libs.coil.compose)
    //genAi
    implementation(libs.generativeai)
    //Room Db
    val room_version = "2.6.1"
    implementation(libs.androidx.room.runtime)
    //Ksp
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    // Live data
    implementation (libs.androidx.runtime)
    implementation (libs.androidx.runtime.livedata)

    //navigation
    implementation (libs.androidx.navigation.compose)
    //serialization
    implementation(libs.kotlinx.serialization.json.v160)
    //Permission
    implementation( libs.accompanist.permissions)
  //fonts
    implementation( libs.androidx.ui.text.google.fonts)
    //extended Icons
    implementation( libs.androidx.material.icons.extended)

}