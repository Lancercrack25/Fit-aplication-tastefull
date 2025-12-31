plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
}

android {
    namespace = "com.example.fat_app"

    // CORRECCIÓN 1: Volvemos al SDK 36, como pide el error
    compileSdk = 36

    defaultConfig {
        applicationId = "com.example.fat_app"

        // Dejamos 26 por tu código de Racha.java (LocalDate)
        minSdk = 26

        // CORRECCIÓN 2: Apuntamos también al SDK 36
        targetSdk = 36

        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        // Tus API Keys (esto está perfecto)
        buildConfigField("String", "USDA_API_KEY", "\"55ozlH3yMfKsqS480BAe0zrA5eWXzUJcLpBJUW3X\"")
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
        // Tu código de Racha.java usa Java 8 (LocalDate)
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        buildConfig = true
    }
}

dependencies {
    // Usamos versiones estables
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("com.google.android.gms:play-services-location:21.0.1")
    implementation("com.stripe:stripe-android:20.39.0")
    implementation("com.google.android.material:material:1.10.0") // O 1.11.0

    // Esta es la que requiere SDK 36, ahora funcionará
    implementation(libs.activity)

    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Konfetti también funcionará una vez que el sync sea exitoso
    implementation("nl.dionsegijn:konfetti-xml:2.0.2")

    // Las de Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}