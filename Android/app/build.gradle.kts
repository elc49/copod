import java.util.Properties

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id("com.google.devtools.ksp")
    id("com.apollographql.apollo3") version "4.0.0-alpha.3"

    id("io.sentry.android.gradle") version "4.10.0"
}

android {
    namespace = "com.lomolo.vuno"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.lomolo.vuno"
        minSdk = 28
        targetSdk = 34
        versionCode = 6
        versionName = "2.0.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }

        val keystoreFile = project.rootProject.file("api.properties")
        val properties = Properties()
        properties.load(keystoreFile.inputStream())

        val localBaseApi = properties.getProperty("LOCAL_BASE_API")
        val prodBaseApi = properties.getProperty("PROD_BASE_API")
        val localWssApi = properties.getProperty("LOCAL_WSS_API")
        val prodWssAPi = properties.getProperty("PROD_WSS_API")
        val localEnv = properties.getProperty("ENV")

        buildConfigField(type="String", name="LOCAL_BASE_API", value=localBaseApi)
        buildConfigField(type="String", name="PROD_BASE_API", value=prodBaseApi)
        buildConfigField(type="String", name="LOCAL_WSS_API", value=localWssApi)
        buildConfigField(type="String", name="PROD_WSS_API", value=prodWssAPi)
        buildConfigField(type="String", name="ENV" , value=localEnv)
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
        buildConfig = true
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

    implementation(libs.androidx.navigation.compose)
    implementation(libs.androidx.room.runtime)
    implementation(libs.androidx.room.ktx)
    implementation(libs.play.services.maps)
    annotationProcessor(libs.androidx.room.compiler)
    ksp(libs.androidx.room.compiler)
    implementation (libs.play.services.location)
    implementation(libs.apollo.runtime)
    implementation(libs.libphonenumber)
    implementation(libs.coil.svg)
    implementation(libs.coil.compose)
    implementation(platform(libs.okhttp.bom))
    implementation(libs.okhttp)
    implementation(libs.converter.moshi)
    implementation(libs.moshi.kotlin)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom.v20240500))
    implementation(libs.material3)
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.ui.text.google.fonts)
    implementation(libs.apollo.normalized.cache.sqlite)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

apollo {
    service("vuno") {
        packageName.set("com.lomolo.vuno")
        generateOptionalOperationVariables.set(false)
    }
}

sentry {
    org.set("uzi-3b")
    projectName.set("lima-app")

    // this will upload your source code to Sentry to show it as part of the stack traces
    // disable if you don't want to expose your sources
    includeSourceContext.set(true)
}
