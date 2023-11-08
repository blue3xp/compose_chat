package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.Conversation
import kotlinx.coroutines.flow.StateFlow

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IConversationProvider {

    val conversationList: StateFlow<List<Conversation>>

    val totalUnreadMessageCount: StateFlow<Long>

    fun refreshConversationList()

    fun refreshTotalUnreadMessageCount()

    fun cleanConversationUnreadMessageCount(chat: Chat)

    suspend fun pinConversation(conversation: Conversation, pin: Boolean): ActionResult

    suspend fun deleteC2CConversation(userId: String): ActionResult

    suspend fun deleteGroupConversation(groupId: String): ActionResult

    suspend fun clear()

}