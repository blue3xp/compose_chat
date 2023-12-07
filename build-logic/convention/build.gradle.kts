import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    `kotlin-dsl`
}

group = "github.leavesczy.compose_chat.build.logic"

java {
    sourceCompatibility = JavaVersion.VERSION_17
    targetCompatibility = JavaVersion.VERSION_17
}

tasks.withType<KotlinCompile>().configureEach {
    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_17.toString()
    }
}

dependencies {
    compileOnly(libs.android.gradle)
    compileOnly(libs.kotlin.gradle)
    compileOnly(libs.leavesczy.trace)
}

gradlePlugin {
    plugins {
        register("androidApplication") {
            id = "compose.android.application"
            implementationClass = "ApplicationConventionPlugin"
        }
        register("androidLibrary") {
            id = "compose.android.library"
            implementationClass = "LibraryConventionPlugin"
        }
        register("androidCompose") {
            id = "compose.android.compose"
            implementationClass = "ComposeConventionPlugin"
        }
        register("kotlinParcelize") {
            id = "compose.kotlin.parcelize"
            implementationClass = "ParcelizeConventionPlugin"
        }
        register("leavesczyTrace") {
            id = "compose.leavesczy.trace"
            implementationClass = "ParcelizeConventionPlugin"
        }
    }
}