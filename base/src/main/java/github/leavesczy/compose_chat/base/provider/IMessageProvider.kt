package github.leavesczy.compose_chat.base.provider

import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.LoadMessageResult
import github.leavesczy.compose_chat.base.models.Message
import kotlinx.coroutines.channels.Channel

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
interface IMessageProvider {

    interface MessageListener {

        fun onReceiveMessage(message: Message)

    }

    fun startReceive(chat: Chat, messageListener: MessageListener)

    fun stopReceive(messageListener: MessageListener)

    suspend fun sendText(chat: Chat, text: String): Channel<Message>

    suspend fun sendImage(chat: Chat, imagePath: String): Channel<Message>

    suspend fun getHistoryMessage(chat: Chat, lastMessage: Message?): LoadMessageResult

    fun cleanConversationUnreadMessageCount(chat: Chat)

}