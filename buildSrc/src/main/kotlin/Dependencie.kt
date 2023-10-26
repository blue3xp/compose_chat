/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object Dependencie {

    object Test {
        const val junit = "junit:junit:4.13.2"
        const val testExt = "androidx.test.ext:junit:1.1.5"
        const val espresso = "androidx.test.espresso:espresso-core:3.5.1"
    }

    object Components {
        const val appcompat = "androidx.appcompat:appcompat:1.6.1"
        const val exifInterface = "androidx.exifinterface:exifinterface:1.3.6"
        const val coroutines = "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3"
        const val matisse = "io.github.leavesczy:matisse:1.1.1"
        const val glide = "com.github.bumptech.glide:compose:1.0.0-beta01"
        const val imSdk = "com.tencent.imsdk:imsdk-plus:7.5.4864"
    }

    object Compose {
        const val compilerVersion = "1.5.3"
        const val bom = "androidx.compose:compose-bom:2023.10.01"
        const val ui = "androidx.compose.ui:ui"
        const val uiTooling = "androidx.compose.ui:ui-tooling"
        const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest"
        const val material = "androidx.compose.material:material"
        const val material3 = "androidx.compose.material3:material3"
        const val materialIcons = "androidx.compose.material:material-icons-extended"
        const val activity = "androidx.activity:activity-compose:1.8.0"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2"
    }

}