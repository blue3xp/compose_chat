import org.gradle.kotlin.dsl.DependencyHandlerScope

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
private fun DependencyHandlerScope.implementationExtend(library: Any) {
    this.add("implementation", library)
}

private fun DependencyHandlerScope.debugImplementationExtend(library: Any) {
    this.add("debugImplementation", library)
}

private fun DependencyHandlerScope.androidTestImplementationExtend(library: Any) {
    this.add("androidTestImplementation", library)
}

private fun DependencyHandlerScope.testImplementationExtend(library: Any) {
    this.add("testImplementation", library)
}

fun DependencyHandlerScope.implementationTest() {
    testImplementationExtend(Dependencie.Test.junit)
    androidTestImplementationExtend(Dependencie.Test.testExt)
    androidTestImplementationExtend(Dependencie.Test.espresso)
}

fun DependencyHandlerScope.implementationCompose() {
    implementationExtend(Dependencie.Compose.ui)
    implementationExtend(Dependencie.Compose.uiToolingPreview)
    debugImplementationExtend(Dependencie.Compose.uiTooling)
    debugImplementationExtend(Dependencie.Compose.uiTestManifest)
    androidTestImplementationExtend(Dependencie.Compose.uiTestJunit4)
    implementationExtend(Dependencie.Compose.material)
    implementationExtend(Dependencie.Compose.material3)
    implementationExtend(Dependencie.Compose.materialIcons)
    implementationExtend(Dependencie.Compose.constraint)
    implementationExtend(Dependencie.Compose.activity)
    implementationExtend(Dependencie.Compose.viewModel)
}

fun DependencyHandlerScope.implementationCoil() {
    implementationExtend(Dependencie.Coil.compose)
    implementationExtend(Dependencie.Coil.gif)
}