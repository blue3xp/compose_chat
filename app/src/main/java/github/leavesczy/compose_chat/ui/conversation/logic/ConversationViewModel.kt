package github.leavesczy.compose_chat.ui.conversation.logic

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.C2CConversation
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.base.models.GroupConversation
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.chat.ChatActivity
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class ConversationViewModel : BaseViewModel() {

    var pageViewState by mutableStateOf(
        value = ConversationPageViewState(
            listState = LazyListState(firstVisibleItemIndex = 0, firstVisibleItemScrollOffset = 0),
            conversationList = emptyList(),
            onClickConversation = ::onClickConversation,
            deleteConversation = ::deleteConversation,
            pinConversation = ::pinConversation
        )
    )
        private set

    init {
        viewModelScope.launch {
            ComposeChat.conversationProvider.conversationList.collect {
                pageViewState = pageViewState.copy(conversationList = it)
            }
        }
        ComposeChat.conversationProvider.refreshConversationList()
        ComposeChat.conversationProvider.refreshTotalUnreadMessageCount()
    }

    private fun onClickConversation(conversation: Conversation) {
        when (conversation) {
            is C2CConversation -> {
                ChatActivity.navTo(
                    context = context,
                    chat = Chat.PrivateChat(id = conversation.id)
                )
            }

            is GroupConversation -> {
                ChatActivity.navTo(
                    context = context,
                    chat = Chat.GroupChat(id = conversation.id)
                )
            }
        }
    }

    private fun deleteConversation(conversation: Conversation) {
        viewModelScope.launch {
            val result = when (conversation) {
                is C2CConversation -> {
                    ComposeChat.conversationProvider.deleteC2CConversation(userId = conversation.id)
                }

                is GroupConversation -> {
                    ComposeChat.conversationProvider.deleteGroupConversation(groupId = conversation.id)
                }
            }
            when (result) {
                is ActionResult.Success -> {
                    ComposeChat.conversationProvider.refreshConversationList()
                }

                is ActionResult.Failed -> {
                    showToast(msg = result.reason)
                }
            }
        }
    }

    private fun pinConversation(conversation: Conversation, pin: Boolean) {
        viewModelScope.launch {
            ComposeChat.conversationProvider.pinConversation(
                conversation = conversation,
                pin = pin
            )
        }
    }

}