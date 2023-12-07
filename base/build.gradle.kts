plugins {
    alias(libs.plugins.compose.android.library)
    alias(libs.plugins.compose.android.compose)
    alias(libs.plugins.compose.kotlin.parcelize)
}

android {
    namespace = "github.leavesczy.compose_chat.base"
}

dependencies {
    implementation(libs.kotlinx.coroutines)
}