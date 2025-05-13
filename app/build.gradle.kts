import java.util.Properties
import java.io.FileInputStream

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)

//    kotlin(libs.plugins.kotlin.serialization.get().pluginId).version(libs.versions.serialization) //.apply(false)

    alias(libs.plugins.serialization)
    alias(libs.plugins.hilt.android)
    alias(libs.plugins.ksp)
}

// Function to safely load properties from local.properties
fun getApiKey(project: Project, propertyName: String): String {
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        val properties = Properties()
        FileInputStream(localPropertiesFile).use { fis ->
            properties.load(fis)
        }
        // Return property value or an empty string if not found
        // The quotes are expected to be part of the value in local.properties
        return properties.getProperty(propertyName, "\"\"")
    }
    // Fallback for CI: Read from environment variable if local.properties doesn't exist or key missing

    // Gradle automatically makes environment variables available as project properties
    // Note: env var names often match property names, but can be different if mapped in CI
    return project.findProperty(propertyName)?.toString() ?: "\"\"" // Default to empty string literal if not found anywhere
}


android {
    namespace = "com.eltonkola.nisi"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.eltonkola.nisi"
        minSdk = 29
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        val pexelsKey = getApiKey(project, "PEXELS_API_KEY")
        val weatherKey = getApiKey(project, "OPENWEATHERMAP_API_KEY")

        buildConfigField("String", "PEXELS_API_KEY", pexelsKey)
        buildConfigField("String", "OPENWEATHERMAP_API_KEY", weatherKey)

    }

    buildTypes {
        release {
            isMinifyEnabled = false
//            isMinifyEnabled = true
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )

            signingConfig = signingConfigs.getByName("debug")
        }


    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
//        sourceCompatibility = JavaVersion.VERSION_1_8
//        targetCompatibility = JavaVersion.VERSION_1_8
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
    implementation(libs.androidx.appcompat)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.tv.foundation)
    implementation(libs.androidx.tv.material)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)

    implementation(libs.androidx.material3)

    implementation(libs.androidx.lifecycle.viewmodel.compose)

    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)

    implementation(libs.kotlinx.coroutines.android)
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.cio)
    implementation(libs.ktor.client.serialization)
    implementation(libs.ktor.serialization.kotlinx.json)
    implementation(libs.ktor.client.content.negotiation)

    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.lifecycle.viewmodel.compose)


    implementation(libs.androidx.datastore.preferences)

    implementation(libs.androidx.navigation.compose)

    implementation(libs.kotlinx.serialization.json)
    implementation(libs.coil.compose)
    implementation(libs.coil.network.okhttp)
    implementation(libs.androidx.lifecycle.runtime.compose)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)

    implementation(libs.hilt.android)
    ksp(libs.hilt.android.compiler)

}