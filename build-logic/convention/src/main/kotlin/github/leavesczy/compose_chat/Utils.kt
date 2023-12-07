package github.leavesczy.compose_chat

import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

private fun getTime(pattern: String): String {
    val simpleDateFormat = SimpleDateFormat(pattern, Locale.US)
    simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
    val time = Calendar.getInstance().time
    return simpleDateFormat.format(time)
}

fun getApkBuildTime(): String {
    return getTime(pattern = "yyyy_MM_dd_HH_mm_ss")
}

fun getBuildConfigTime(): String {
    return getTime(pattern = "yyyy-MM-dd HH:mm:ss")
}