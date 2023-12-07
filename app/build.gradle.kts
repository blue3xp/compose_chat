plugins {
    alias(libs.plugins.compose.android.application)
    alias(libs.plugins.compose.android.compose)
    alias(libs.plugins.compose.kotlin.parcelize)
    alias(libs.plugins.compose.leavesczy.trace)
}

android {
    namespace = "github.leavesczy.compose_chat"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":proxy"))
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.lifecycle.viewmodel.compose)
    implementation(libs.androidx.exifinterface)
    implementation(libs.kotlinx.coroutines)
    implementation(libs.leavesczy.matisse)
    implementation(libs.glide.compose)
}