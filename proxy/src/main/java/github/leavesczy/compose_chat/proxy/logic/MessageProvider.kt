package github.leavesczy.compose_chat.proxy.logic

import android.graphics.BitmapFactory
import com.tencent.imsdk.v2.V2TIMAdvancedMsgListener
import com.tencent.imsdk.v2.V2TIMManager
import com.tencent.imsdk.v2.V2TIMMessage
import com.tencent.imsdk.v2.V2TIMSendCallback
import com.tencent.imsdk.v2.V2TIMValueCallback
import github.leavesczy.compose_chat.base.model.Chat
import github.leavesczy.compose_chat.base.model.ImageElement
import github.leavesczy.compose_chat.base.model.ImageMessage
import github.leavesczy.compose_chat.base.model.LoadMessageResult
import github.leavesczy.compose_chat.base.model.Message
import github.leavesczy.compose_chat.base.model.MessageDetail
import github.leavesczy.compose_chat.base.model.MessageState
import github.leavesczy.compose_chat.base.model.PersonProfile
import github.leavesczy.compose_chat.base.model.SystemMessage
import github.leavesczy.compose_chat.base.model.TextMessage
import github.leavesczy.compose_chat.base.model.TimeMessage
import github.leavesczy.compose_chat.base.provider.IMessageProvider
import github.leavesczy.compose_chat.proxy.coroutine.ChatCoroutineScope
import github.leavesczy.compose_chat.proxy.utils.Converters
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import java.lang.ref.SoftReference
import kotlin.coroutines.resume
import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class MessageProvider : IMessageProvider {

    private val messageListenerMap =
        mutableMapOf<String, SoftReference<IMessageProvider.MessageListener>>()

    init {
        V2TIMManager.getMessageManager()
            .addAdvancedMsgListener(
                object : V2TIMAdvancedMsgListener() {
                    override fun onRecvNewMessage(msg: V2TIMMessage) {
                        val partId = msg.groupID ?: msg.userID ?: ""
                        val messageListener = messageListenerMap[partId]?.get()
                        if (messageListener != null) {
                            val message = Converters.convertMessage(msg)
                            messageListener.onReceiveMessage(
                                message = message
                            )
                        } else {
                            messageListenerMap.remove(partId)
                        }
                    }
                })
    }

    override suspend fun getHistoryMessage(chat: Chat, lastMessage: Message?): LoadMessageResult {
        val chatId = chat.id
        val count = 60
        return suspendCancellableCoroutine { continuation ->
            val callback = object : V2TIMValueCallback<List<V2TIMMessage>> {
                override fun onSuccess(t: List<V2TIMMessage>) {
                    continuation.resume(
                        value = LoadMessageResult.Success(
                            messageList = Converters.convertMessage(t), loadFinish = t.size < count
                        )
                    )
                }

                override fun onError(code: Int, desc: String?) {
                    continuation.resume(
                        value = LoadMessageResult.Failed(reason = "code: $code desc: $desc")
                    )
                }
            }
            when (chat) {
                is Chat.PrivateChat -> {
                    V2TIMManager.getMessageManager().getC2CHistoryMessageList(
                        chatId,
                        count,
                        lastMessage?.tag as? V2TIMMessage, callback
                    )
                }

                is Chat.GroupChat -> {
                    V2TIMManager.getMessageManager().getGroupHistoryMessageList(
                        chatId,
                        count,
                        lastMessage?.tag as? V2TIMMessage, callback
                    )
                }
            }
        }
    }

    override suspend fun sendText(chat: Chat, text: String): Channel<Message> {
        val localTempMessage = TextMessage(detail = generatePreSendMessageDetail(), text = text)
        val createdMessage = V2TIMManager.getMessageManager().createTextMessage(text)
        return sendMessage(
            chat = chat,
            timMessage = createdMessage,
            localTempMessage = localTempMessage
        )
    }

    override suspend fun sendImage(chat: Chat, imagePath: String): Channel<Message> {
        return withContext(context = Dispatchers.Default) {
            val options = BitmapFactory.Options()
            options.inJustDecodeBounds = true
            BitmapFactory.decodeFile(imagePath, options)
            val localTempMessage = ImageMessage(
                detail = generatePreSendMessageDetail(),
                original = ImageElement(options.outWidth, options.outHeight, imagePath),
                large = null,
                thumb = null
            )
            val createdMessage = V2TIMManager.getMessageManager().createImageMessage(imagePath)
            return@withContext sendMessage(
                chat = chat,
                timMessage = createdMessage,
                localTempMessage = localTempMessage
            )
        }
    }

    private suspend fun sendMessage(
        chat: Chat,
        timMessage: V2TIMMessage,
        localTempMessage: Message
    ): Channel<Message> {
        val messageChannel = Channel<Message>(capacity = 2)
        val c2cId: String
        val groupId: String
        when (chat) {
            is Chat.PrivateChat -> {
                c2cId = chat.id
                groupId = ""
            }

            is Chat.GroupChat -> {
                c2cId = ""
                groupId = chat.id
            }
        }
        messageChannel.send(localTempMessage)
        V2TIMManager.getMessageManager().sendMessage(timMessage,
            c2cId,
            groupId,
            V2TIMMessage.V2TIM_PRIORITY_HIGH,
            false,
            null,
            object : V2TIMSendCallback<V2TIMMessage> {
                override fun onSuccess(messsage: V2TIMMessage) {
                    ChatCoroutineScope.launch {
                        val convertMessage = Converters.convertMessage(messsage)
                        messageChannel.send(element = convertMessage)
                        messageChannel.close()
                    }
                }

                override fun onError(code: Int, desc: String?) {
                    ChatCoroutineScope.launch {
                        messageChannel.send(element = localTempMessage.resetToFailed(failReason = "code: $code desc: $desc"))
                        messageChannel.close()
                    }
                }

                override fun onProgress(progress: Int) {

                }
            })
        return messageChannel
    }

    private fun Message.resetToFailed(failReason: String): Message {
        val failedState = MessageState.SendFailed(reason = failReason)
        return when (this) {
            is TextMessage -> {
                this.copy(detail = this.messageDetail.copy(state = failedState))
            }

            is ImageMessage -> {
                this.copy(detail = this.messageDetail.copy(state = failedState))
            }

            is TimeMessage -> {
                throw IllegalArgumentException()
            }

            is SystemMessage -> {
                throw IllegalArgumentException()
            }
        }
    }

    override suspend fun uploadImage(chat: Chat, imagePath: String): String {
        return suspendCancellableCoroutine { continuation ->
            val imageMessage = V2TIMManager.getMessageManager().createImageMessage(imagePath)
            V2TIMManager.getMessageManager().sendMessage(imageMessage,
                "",
                chat.id,
                V2TIMMessage.V2TIM_PRIORITY_HIGH,
                false,
                null,
                object : V2TIMSendCallback<V2TIMMessage> {
                    override fun onSuccess(message: V2TIMMessage) {
                        val res = Converters.convertMessage(message)
                        continuation.resume((res as? ImageMessage)?.previewImageUrl ?: "")
                    }

                    override fun onError(code: Int, desc: String?) {
                        continuation.resume("")
                    }

                    override fun onProgress(progress: Int) {

                    }
                })
        }
    }

    override fun startReceive(chat: Chat, messageListener: IMessageProvider.MessageListener) {
        val id = chat.id
        messageListenerMap.remove(id)
        messageListenerMap[id] = SoftReference(messageListener)
        checkListener()
    }

    override fun stopReceive(messageListener: IMessageProvider.MessageListener) {
        removeReduceListener(listener = messageListener, listenerMap = messageListenerMap)
    }

    private fun checkListener() {
        removeReduceListener(listener = null, listenerMap = messageListenerMap)
    }

    private fun removeReduceListener(
        listener: IMessageProvider.MessageListener?,
        listenerMap: MutableMap<String, SoftReference<IMessageProvider.MessageListener>>
    ) {
        val filter = listenerMap.filter { it.value.get() == listener }
        for (entry in filter) {
            listenerMap.remove(entry.key)
        }
    }

    private suspend fun generatePreSendMessageDetail(): MessageDetail {
        return MessageDetail(
            msgId = generateMessageId(),
            timestamp = generateMessageTimestamp(),
            state = MessageState.Sending,
            sender = Converters.getSelfProfile() ?: PersonProfile.Empty,
            isOwnMessage = true
        )
    }

    private fun generateMessageId(): String {
        return (System.currentTimeMillis() + Random.nextInt(
            1024, 2048
        )).toString()
    }

    private fun generateMessageTimestamp(): Long {
        return V2TIMManager.getInstance().serverTime
    }

}