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
        const val matisse = "com.github.leavesCZY:Matisse:0d2e8b1455"
        const val imSdk = "com.tencent.imsdk:imsdk-plus:7.5.4852"
    }

    object Compose {
        const val compilerVersion = "1.5.3"
        private const val composeVersion = "1.6.0-alpha07"
        const val ui = "androidx.compose.ui:ui:${composeVersion}"
        const val uiTooling = "androidx.compose.ui:ui-tooling:${composeVersion}"
        const val uiToolingPreview = "androidx.compose.ui:ui-tooling-preview:${composeVersion}"
        const val uiTestJunit4 = "androidx.compose.ui:ui-test-junit4:${composeVersion}"
        const val uiTestManifest = "androidx.compose.ui:ui-test-manifest:${composeVersion}"
        const val material = "androidx.compose.material:material:${composeVersion}"
        const val materialIcons = "androidx.compose.material:material-icons-extended:${composeVersion}"
        const val material3 = "androidx.compose.material3:material3:1.2.0-alpha09"
        const val constraint = "androidx.constraintlayout:constraintlayout-compose:1.1.0-alpha13"
        const val activity = "androidx.activity:activity-compose:1.8.0"
        const val viewModel = "androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2"
    }

    object Coil {
        private const val coilVersion = "2.4.0"
        const val compose = "io.coil-kt:coil-compose:$coilVersion"
        const val gif = "io.coil-kt:coil-gif:$coilVersion"
    }

}