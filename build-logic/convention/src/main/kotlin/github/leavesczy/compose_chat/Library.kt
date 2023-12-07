package github.leavesczy.compose_chat

import com.android.build.gradle.LibraryExtension
import org.gradle.api.Project
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.kotlin.dsl.getByType
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

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