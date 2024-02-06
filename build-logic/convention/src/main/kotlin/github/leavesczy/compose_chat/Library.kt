package github.leavesczy.compose_chat

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import java.io.File

/**
 * @Author: leavesCZY
 * @Date: 2023/11/29 16:10
 * @Desc:
 */
internal fun Project.configureAndroidLibrary(commonExtension: LibraryExtension) {
    commonExtension.apply {
        defaultConfig {
            consumerProguardFiles.add(File("consumer-rules.pro"))
        }
    }
}