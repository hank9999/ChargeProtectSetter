import java.util.Properties

plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
}

val versions = mapOf(
    "api" to "82",
    "dexkit" to "2.0.2",
    "hooktool" to "v.1.1.3",
    "annotation" to "1.9.1",
    "preference" to "1.2.1"
)

android {
    val properties = Properties()
    val inputStream = project.rootProject.file("local.properties").inputStream()
    properties.load(inputStream)

    val keyStoreFile = file(properties.getProperty("keyStoreFile"))
    val keyStorePassword = properties.getProperty("keyStorePassword")
    val keyAlias = properties.getProperty("keyAlias")
    val keyAliasPassword = properties.getProperty("keyAliasPassword")

    signingConfigs {
        create("release") {
            this.storeFile = keyStoreFile
            this.storePassword = keyStorePassword
            this.keyAlias = keyAlias
            this.keyPassword = keyAliasPassword
            this.enableV2Signing = true
            this.enableV3Signing = true
            this.enableV4Signing = true
        }
    }

    namespace = "com.github.hank9999.chargeprotectsetter"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.github.hank9999.chargeprotectsetter"
        minSdk = 29
        targetSdk = 34
        versionCode = 24122301
        versionName = "1.0"
        ndk {
            abiFilters.addAll(listOf("arm64-v8a", "armeabi-v7a"))
        }
        signingConfig = signingConfigs.getByName("release")
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = true
            signingConfig = signingConfigs.getByName("release")
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                file("proguard-rules.pro")
            )
        }
        getByName("debug") {
            isMinifyEnabled = false
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    packaging {
        resources {
            excludes.add("/META-INF/**")
            excludes.add("/kotlin/**")
            excludes.add("/*.txt")
            excludes.add("/*.bin")
            excludes.add("/*.json")
        }
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    compileOnly("de.robv.android.xposed:api:${versions["api"]}")
    implementation("org.luckypray:dexkit:${versions["dexkit"]}")
    implementation("com.github.HChenX:HookTool:${versions["hooktool"]}")
    implementation("androidx.annotation:annotation:${versions["annotation"]}")
    implementation("androidx.preference:preference-ktx:${versions["preference"]}")
}