import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.parcelize)
    alias(libs.plugins.leavesczy.trace)
}

android {
    namespace = "github.leavesczy.compose_chat"
    compileSdk = libs.versions.app.compile.sdk.get().toInt()
    buildToolsVersion = libs.versions.app.build.tools.version.get()
    defaultConfig {
        applicationId = libs.versions.app.application.id.get()
        minSdk = libs.versions.app.min.sdk.get().toInt()
        targetSdk = libs.versions.app.target.sdk.get().toInt()
        versionCode = libs.versions.app.version.code.get().toInt()
        versionName = libs.versions.app.version.name.get()
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        resourceConfigurations.add("zh")
        vectorDrawables {
            useSupportLibrary = true
        }
        applicationVariants.all {
            val variant = this
            outputs.all {
                if (this is com.android.build.gradle.internal.api.ApkVariantOutputImpl) {
                    this.outputFileName =
                        "compose_chat_${variant.name}_versionCode_${variant.versionCode}_versionName_${variant.versionName}_${getApkBuildTime()}.apk"
                }
            }
        }
        buildConfigField("String", "VERSION_NAME", "\"${libs.versions.app.version.name.get()}\"")
        buildConfigField("String", "BUILD_TIME", "\"${getBuildConfigTime()}\"")
    }
    signingConfigs {
        create("release") {
            storeFile =
                File(rootDir.absolutePath + File.separator + "doc" + File.separator + "key.jks")
            keyAlias = libs.versions.app.key.alias.get()
            keyPassword = libs.versions.app.key.password.get()
            storePassword = libs.versions.app.store.password.get()
            enableV1Signing = true
            enableV2Signing = true
            enableV3Signing = true
            enableV4Signing = true
        }
    }
    buildTypes {
        debug {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            isShrinkResources = false
            isDebuggable = true
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                abiFilters.apply {
                    add("arm64-v8a")
                    add("x86")
                }
            }
        }
        release {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = true
            isShrinkResources = true
            isDebuggable = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            ndk {
                abiFilters.add("arm64-v8a")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        compose = true
        buildConfig = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    kotlinOptions {
        jvmTarget = "11"
        freeCompilerArgs += setOf(
            "-opt-in=androidx.compose.ui.ExperimentalComposeUiApi",
            "-opt-in=androidx.compose.animation.ExperimentalAnimationApi",
            "-opt-in=androidx.compose.foundation.ExperimentalFoundationApi",
            "-opt-in=androidx.compose.foundation.layout.ExperimentalLayoutApi",
            "-opt-in=androidx.compose.material.ExperimentalMaterialApi",
            "-opt-in=androidx.compose.material3.ExperimentalMaterial3Api",
        )
    }
    packaging {
        jniLibs {
            excludes += setOf("META-INF/{AL2.0,LGPL2.1}")
        }
        resources {
            excludes += setOf(
                "**/*.md",
                "**/*.version",
                "**/*.properties",
                "**/**/*.properties",
                "META-INF/{AL2.0,LGPL2.1}",
                "META-INF/CHANGES",
                "DebugProbesKt.bin",
                "kotlin-tooling-metadata.json"
            )
        }
    }
}

dependencies {
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.androidx.junit)
    androidTestImplementation(libs.test.androidx.espresso)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.exifinterface)
    val composePlatform = platform(libs.compose.bom)
    implementation(composePlatform)
    androidTestImplementation(composePlatform)
    implementation(libs.compose.ui)
    debugImplementation(libs.compose.ui.tooling)
    debugImplementation(libs.compose.ui.test.manifest)
    implementation(libs.compose.ui.tooling.preview)
    androidTestImplementation(libs.compose.ui.test.junit4)
    implementation(libs.compose.runtime)
    implementation(libs.compose.material)
    implementation(libs.compose.material.icons)
    implementation(libs.compose.material3)
    implementation(libs.jetbrains.coroutines)
    implementation(libs.leavesczy.matisse)
    implementation(libs.glide.compose)
    implementation(project(":base"))
    implementation(project(":proxy"))
}

composeClickTrace {
    onClickClass = "github.leavesczy.compose_chat.extend.ComposeOnClick"
    onClickWhiteList = "notCheck"
}

fun getTime(pattern: String): String {
    val simpleDateFormat = SimpleDateFormat(pattern)
    simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
    val time = Calendar.getInstance().time
    return simpleDateFormat.format(time)
}

fun getApkBuildTime(): String {
    return getTime("yyyy_MM_dd_HH_mm_ss")
}

fun getBuildConfigTime(): String {
    return getTime("yyyy-MM-dd HH:mm:ss")
}