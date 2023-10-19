android {
    namespace = "github.leavesczy.compose_chat"
}

dependencies {
    implementation(project(":base"))
    implementation(project(":proxy"))
    implementationTest()
    implementationCompose()
    implementation(Dependencie.Components.appcompat)
    implementation(Dependencie.Components.exifInterface)
    implementation(Dependencie.Components.coroutines)
    implementation(Dependencie.Components.matisse)
    implementation(Dependencie.Components.glide)
    implementation(Dependencie.Components.zoomable)
}

composeClickTrace {
    onClickClass = "github.leavesczy.compose_chat.extend.ComposeOnClick"
    onClickWhiteList = "notCheck"
}