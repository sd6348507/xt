import com.roemsoft.equipment.plugins.*

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

android {
    namespace = "com.roemsoft.equipment"
    compileSdk = BuildConfig.compileSdk

    defaultConfig {
        applicationId = "com.roemsoft.equipment"

        minSdk = BuildConfig.minSdk
        targetSdk = BuildConfig.targetSdk
        versionCode = BuildConfig.versionCode
        versionName = BuildConfig.versionName
    }

    signingConfigs {
        create("release") {
            storeFile = file("doc/roem.jks")
            storePassword = if (project.hasProperty("KEYSTORE_PASS")) {
                project.property("KEYSTORE_PASS") as String
            } else {
                System.getenv("KEYSTORE_PASS")
            }
            keyAlias = System.getenv("ALIAS_NAME")
            keyPassword = System.getenv("ALIAS_PASS")
            enableV1Signing = true
            enableV2Signing = true
        }
    }

    buildTypes {
        debug {
            isDebuggable = true
            isMinifyEnabled = false
            multiDexEnabled = true
            signingConfig = signingConfigs.getByName("release")
        }

        release {
            isDebuggable = false
        //    isShrinkResources = true
            isMinifyEnabled = false
            multiDexEnabled = true
            signingConfig = signingConfigs.getByName("release")
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

    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        dataBinding = true
    }

    viewBinding {
        enable = true
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }

    android.applicationVariants.all {
        outputs.all {
            val version = defaultConfig.versionName
            val buildType = buildType.name
            (this as com.android.build.gradle.internal.api.BaseVariantOutputImpl).outputFileName =
                "xt-${buildType}-v${version}.apk"
        }
    }

    dependencies {
        implementation(project(path = ":common"))
        implementation(project(path = ":downloadlib"))
        implementation(files("libs/LPAPI-2024-08-02-R.jar"))

        implementation("androidx.core:core-ktx:1.10.1")
        implementation("androidx.appcompat:appcompat:1.6.1")
        implementation("com.google.android.material:material:1.9.0")
        implementation("androidx.constraintlayout:constraintlayout:2.1.4")
        implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
        implementation("androidx.activity:activity-ktx:1.7.0")

        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.5.0")
        implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

        implementation("com.squareup.retrofit2:retrofit:2.9.0")
    //    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
        implementation("com.squareup.retrofit2:converter-moshi:2.9.0")
        implementation("com.squareup.okhttp3:logging-interceptor:4.9.1")

        implementation("com.github.sd6348507:zltd_scanner:1.0.13")

        implementation("com.jakewharton.timber:timber:4.7.1")

        implementation("com.guolindev.permissionx:permissionx:1.7.1")

        implementation("io.github.jeremyliao:live-event-bus-x:1.8.0")

        implementation("jp.wasabeef:recyclerview-animators:4.0.2")

        implementation("androidx.camera:camera-camera2:1.2.2")
        implementation("androidx.camera:camera-lifecycle:1.2.2")
        implementation("androidx.camera:camera-view:1.2.2")

        implementation("com.google.mlkit:barcode-scanning:17.2.0")
    }
}
