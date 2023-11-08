plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "github.leavesczy.compose_chat.proxy"
    compileSdk = libs.versions.app.compile.sdk.get().toInt()
    buildToolsVersion = libs.versions.app.build.tools.version.get()
    defaultConfig {
        consumerProguardFiles.add(File("consumer-rules.pro"))
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    testImplementation(libs.test.junit)
    androidTestImplementation(libs.test.androidx.junit)
    androidTestImplementation(libs.test.androidx.espresso)
    implementation(libs.jetbrains.coroutines)
    implementation(libs.tencent.imsdk)
    implementation(project(":base"))
}