package github.leavesczy.compose_chat.ui.chat.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.proxy.logic.GroupProvider
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class GroupProfileViewModel(private val groupId: String) : BaseViewModel() {

    private val groupProvider: IGroupProvider = GroupProvider()

    var pageViewState by mutableStateOf<GroupProfilePageViewState?>(
        value = null
    )
        private set

    var loadingDialogVisible by mutableStateOf(value = false)
        private set

    init {
        viewModelScope.launch {
            loadingDialog(visible = true)
            val groupProfileAsync = async {
                groupProvider.getGroupInfo(groupId = groupId)
            }
            val memberListAsync = async {
                groupProvider.getGroupMemberList(groupId = groupId)
            }
            val groupProfile = groupProfileAsync.await()
            if (groupProfile != null) {
                pageViewState = GroupProfilePageViewState(
                    groupProfile = groupProfile,
                    memberList = memberListAsync.await()
                )
            }
            loadingDialog(visible = false)
        }
    }

    private fun getGroupProfile() {
        viewModelScope.launch {
            groupProvider.getGroupInfo(groupId = groupId)?.let {
                pageViewState = pageViewState?.copy(groupProfile = it)
            }
        }
    }

    suspend fun quitGroup(): ActionResult {
        return groupProvider.quitGroup(groupId = groupId)
    }

    fun setAvatar(avatarUrl: String) {
        viewModelScope.launch {
            when (val result =
                groupProvider.setAvatar(groupId = groupId, avatarUrl = avatarUrl)) {
                ActionResult.Success -> {
                    getGroupProfile()
                    showToast(msg = "修改成功")
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
        }
    }

    private fun loadingDialog(visible: Boolean) {
        loadingDialogVisible = visible
    }

}