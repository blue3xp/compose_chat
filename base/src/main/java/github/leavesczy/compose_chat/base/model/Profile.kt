package github.leavesczy.compose_chat.base.model

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.utils.TimeUtil

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
data class PersonProfile(
    val id: String,
    val faceUrl: String,
    val nickname: String,
    val remark: String,
    val signature: String,
    val isFriend: Boolean = false
) {

    companion object {

        val Empty = PersonProfile(
            id = "",
            faceUrl = "",
            nickname = "",
            remark = "",
            signature = "",
            isFriend = false
        )

    }

    val showName: String
        get() {
            return remark.ifBlank {
                nickname.ifBlank {
                    id
                }
            }
        }

}

@Stable
class GroupMemberProfile(
    val detail: PersonProfile,
    val role: String,
    val isOwner: Boolean,
    val joinTime: Long
) {

    val joinTimeFormat =
        TimeUtil.formatTime(time = joinTime * 1000, format = TimeUtil.YYYY_MM_DD_HH_MM_SS)

}

@Stable
class GroupProfile(
    val id: String,
    val faceUrl: String,
    val name: String,
    val introduction: String,
    val createTime: Long,
    val memberCount: Int
) {

    val createTimeFormat =
        TimeUtil.formatTime(time = createTime * 1000, format = TimeUtil.YYYY_MM_DD_HH_MM_SS)

}