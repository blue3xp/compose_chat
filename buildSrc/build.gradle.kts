repositories {
    gradlePluginPortal()
    google()
    mavenCentral()
}

plugins {
    `kotlin-dsl`
}

dependencies {
    val agpVersion = "8.1.1"
    val kotlinVersion = "1.9.10"
    val traceVersion = "0.0.3"
    implementation("com.android.tools.build:gradle-api:${agpVersion}")
    implementation("com.android.tools.build:gradle:${agpVersion}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin:${kotlinVersion}")
    implementation("org.jetbrains.kotlin:kotlin-gradle-plugin-api:${kotlinVersion}")
    implementation("io.github.leavesczy:trace:${traceVersion}")
}