android {
    namespace = "github.leavesczy.compose_chat"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":proxy"))
    implementationTest()
    implementationCompose()
    implementationCoil()
    implementation(Dependencie.Components.appcompat)
    implementation(Dependencie.Components.exifInterface)
    implementation(Dependencie.Components.coroutines)
    implementation(Dependencie.Components.matisse)
}

composeClickTrace {
    onClickClass = "github.leavesczy.compose_chat.extend.ComposeOnClick"
    onClickWhiteList = "notCheck"
}