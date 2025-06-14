import java.util.Properties

plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.room)
    alias(libs.plugins.google.devtools.ksp)
}

android {
    namespace = "com.example.pianomemo"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.pianomemo"
        minSdk = 23
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        // local.properties ファイルのパスを取得
        val localPropertiesFile = rootProject.file("local.properties")
        val localProperties = Properties()

        // local.properties ファイルが存在する場合、プロパティを読み込む
        if (localPropertiesFile.exists()) {
            localPropertiesFile.inputStream().use { stream ->
                localProperties.load(stream)
            }
        }

        // authToken プロパティを取得
        val authToken: String = localProperties.getProperty("authToken") ?: ""
        // BuildConfig に authToken を設定
        buildConfigField("String", "AUTH_TOKEN", "\"$authToken\"")

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
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
    room {
        schemaDirectory("$projectDir/schemas")
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
    // splash
    implementation("androidx.core:core-splashscreen:1.0.1")

    // room
    implementation(libs.androidx.room.runtime)
    ksp(libs.androidx.room.compiler)
    implementation(libs.androidx.room.ktx)
    annotationProcessor(libs.androidx.room.compiler)

    // retrofit
    implementation("com.squareup.retrofit2:retrofit:2.11.0")
    implementation("com.squareup.retrofit2:converter-gson:2.11.0")
    implementation("com.google.code.gson:gson:2.11.0")

    // datastore
    implementation("androidx.datastore:datastore-preferences:1.1.7")

    // icons-extended
    implementation("androidx.compose.material:material-icons-extended:1.7.8")

    // material
    implementation(libs.androidx.material)
    implementation("androidx.compose.material3:material3")

    // appcompat
    implementation("androidx.appcompat:appcompat:1.7.1")

    // accompanist-pager
    implementation("com.google.accompanist:accompanist-pager-indicators:0.27.1")
    implementation("com.google.accompanist:accompanist-pager:0.27.1")

    // navigation
    implementation(libs.androidx.navigation.compose.v)
    implementation(libs.androidx.navigation.compose)

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}