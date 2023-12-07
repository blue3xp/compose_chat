plugins {
    alias(libs.plugins.compose.android.library)
}

android {
    namespace = "github.leavesczy.compose_chat.proxy"
}

dependencies {
    implementation(project(":base"))
    implementation(libs.kotlinx.coroutines)
    implementation(libs.tencent.imsdk)
}