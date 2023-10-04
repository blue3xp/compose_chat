android {
    namespace = "github.leavesczy.compose_chat.proxy"
}

dependencies {
    implementation(project(":base"))
    implementationTest()
    implementation(Dependencie.Components.coroutines)
    implementation(Dependencie.Components.imSdk)
}