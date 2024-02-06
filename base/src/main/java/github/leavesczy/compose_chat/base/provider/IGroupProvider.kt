package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.GroupMemberProfile
import github.leavesczy.compose_chat.base.models.GroupProfile
import kotlinx.collections.immutable.ImmutableList
import kotlinx.coroutines.flow.SharedFlow

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IGroupProvider {

    val joinedGroupList: SharedFlow<ImmutableList<GroupProfile>>

    fun refreshJoinedGroupList()

    suspend fun joinGroup(groupId: String): ActionResult

    suspend fun quitGroup(groupId: String): ActionResult

    suspend fun getGroupInfo(groupId: String): GroupProfile?

    suspend fun getGroupMemberList(groupId: String): ImmutableList<GroupMemberProfile>

    suspend fun setAvatar(groupId: String, avatarUrl: String): ActionResult

    suspend fun transferGroupOwner(groupId: String, newOwnerUserID: String): ActionResult

}