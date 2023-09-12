import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
object BuildFunction {

    private fun getTime(pattern: String): String {
        val simpleDateFormat = SimpleDateFormat(pattern)
        simpleDateFormat.timeZone = TimeZone.getTimeZone("Asia/Shanghai")
        val time = Calendar.getInstance().time
        return simpleDateFormat.format(time)
    }

    fun getApkBuildTime(): String {
        return getTime("yyyy_MM_dd_HH_mm_ss")
    }

    fun getBuildConfigTime(): String {
        return getTime("yyyy-MM-dd HH:mm:ss")
    }

}