package github.leavesczy.compose_chat.store.chat

import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.lifecycle.viewModelScope
import com.example.reduxforandroid.redux.Dispatcher
import com.example.reduxforandroid.redux.Reducer
import com.example.reduxforandroid.redux.middleware.Thunk
import github.leavesczy.compose_chat.base.models.Chat
import github.leavesczy.compose_chat.base.models.ImageMessage
import github.leavesczy.compose_chat.base.models.LoadMessageResult
import github.leavesczy.compose_chat.base.models.Message
import github.leavesczy.compose_chat.base.models.MessageState
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.base.models.SystemMessage
import github.leavesczy.compose_chat.base.models.TextMessage
import github.leavesczy.compose_chat.base.models.TimeMessage
import github.leavesczy.compose_chat.base.provider.IFriendshipProvider
import github.leavesczy.compose_chat.base.provider.IGroupProvider
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.base.store.account.AccountState
import github.leavesczy.compose_chat.base.store.account.LoginSuccess
import github.leavesczy.compose_chat.base.store.account.UpdatePersonProfile
import github.leavesczy.compose_chat.base.store.account.UserLogout
import github.leavesczy.compose_chat.proxy.logic.FriendshipProvider
import github.leavesczy.compose_chat.proxy.logic.GroupProvider
import github.leavesczy.compose_chat.store.store
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class ChatState(
    val allMessage:MutableList<Message> = mutableListOf(),
    val topBarTitle:String = "",
    val loadFinish: Boolean = false
)



data class SetTopBarTitle(val title: String)
data class SetLoadFinish(val loadFinish: Boolean)
data class SetAllMessage(val allMessage: MutableList<Message>)
data class ResetMessageState(val msgId: String, val messageState: MessageState)
data class AttachNewMessage(val newMessage: Message)
class ResetData()

val chatReducer: Reducer<ChatState> = { state, action ->
    when (action) {
        is SetTopBarTitle -> state.copy(
            topBarTitle = action.title,
        )
        is SetLoadFinish -> state.copy(
            loadFinish = action.loadFinish,
        )
        is SetAllMessage -> state.copy(
            allMessage = action.allMessage,
        )
        is ResetData -> state.copy(
            allMessage = mutableListOf(),
            topBarTitle = "",
            loadFinish = false
        )
        is ResetMessageState -> resetMessageStateHandle(state,action)
        is AttachNewMessage -> attachNewMessageHandle(state,action)
        else -> state
    }
}

fun attachNewMessageHandle(state: ChatState, action: AttachNewMessage):ChatState{
    val newList = state.allMessage.toMutableList()
    val firstMessage = state.allMessage.getOrNull(0)
    if (firstMessage == null || action.newMessage.detail.timestamp - firstMessage.detail.timestamp > 60) {
        newList.add(0, TimeMessage(targetMessage = action.newMessage))
    }
    newList.add(0, action.newMessage)
    return state.copy(allMessage = newList);
}

fun resetMessageStateHandle(state: ChatState, action: ResetMessageState):ChatState{
    val newList = state.allMessage.toMutableList()
    val index = state.allMessage.indexOfFirst { it.detail.msgId == action.msgId }
    if (index >= 0) {
        val targetMessage = state.allMessage[index]
        val messageDetail = targetMessage.detail
        val newMessage = when (targetMessage) {
            is ImageMessage -> {
                targetMessage.copy(messageDetail = messageDetail.copy(state = action.messageState))
            }

            is TextMessage -> {
                targetMessage.copy(messageDetail = messageDetail.copy(state = action.messageState))
            }

            is SystemMessage, is TimeMessage -> {
                throw IllegalArgumentException()
            }
        }
        newList[index] = newMessage
        return state.copy(allMessage = newList);
    }
    return state
}

fun getTopBarTitle(viewModelScope: CoroutineScope,chat:Chat): Thunk<AccountState> = { dispatch, getState, extraArg ->
    viewModelScope.launch {
        val name = when (chat) {
            is Chat.PrivateChat -> {
                val friendshipProvider: IFriendshipProvider = FriendshipProvider()
                friendshipProvider.getFriendProfile(friendId = chat.id)?.showName
            }

            is Chat.GroupChat -> {
                val groupProvider: IGroupProvider = GroupProvider()
                groupProvider.getGroupInfo(groupId = chat.id)?.name
            }
        } ?: ""
        dispatch(SetTopBarTitle(name))
    }
}

fun loadMoreMessage(viewModelScope: CoroutineScope,messageProvider: IMessageProvider,lastMessage: Message?,chat:Chat): Thunk<AccountState> = { dispatch, getState, extraArg ->
    viewModelScope.launch {
        val loadResult = messageProvider.getHistoryMessage(
            chat = chat,
            lastMessage = lastMessage
        )
        val loadFinish = when (loadResult) {
            is LoadMessageResult.Success -> {
                addMessageToFooter(newMessageList = loadResult.messageList,dispatch)
                loadResult.loadFinish
            }

            is LoadMessageResult.Failed -> {
                false
            }
        }
        dispatch(SetLoadFinish(loadFinish))
    }
}

private fun addMessageToFooter(newMessageList: List<Message>,dispatch: Dispatcher) {
    val allMessage = store.state.chatState.allMessage.toMutableList()
    if (newMessageList.isNotEmpty()) {
        if (allMessage.isNotEmpty()) {
            if (allMessage[allMessage.size - 1].detail.timestamp - newMessageList[0].detail.timestamp > 60) {
                allMessage.add(TimeMessage(targetMessage = allMessage[allMessage.size - 1]))
            }
        }
        var filteredMsg = 1
        for (index in newMessageList.indices) {
            val currentMsg = newMessageList[index]
            val preMsg = newMessageList.getOrNull(index + 1)
            allMessage.add(currentMsg)
            if (preMsg == null || currentMsg.detail.timestamp - preMsg.detail.timestamp > 60 || filteredMsg >= 10) {
                allMessage.add(TimeMessage(targetMessage = currentMsg))
                filteredMsg = 1
            } else {
                filteredMsg++
            }
        }
        dispatch(SetAllMessage(allMessage))
    }
}