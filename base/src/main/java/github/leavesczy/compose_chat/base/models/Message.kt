package github.leavesczy.compose_chat.base.models

import androidx.compose.runtime.Stable
import github.leavesczy.compose_chat.base.utils.TimeUtil

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Stable
sealed class MessageState {

    data object Sending : MessageState()

    class SendFailed(val reason: String) : MessageState()

    data object Completed : MessageState()

}

@Stable
data class MessageDetail(
    val msgId: String,
    val timestamp: Long,
    val state: MessageState,
    val sender: PersonProfile,
    val isOwnMessage: Boolean
) {

    val conversationTime by lazy(mode = LazyThreadSafetyMode.NONE) {
        TimeUtil.toConversationTime(timeStamp = timestamp)
    }

    val chatTime by lazy(mode = LazyThreadSafetyMode.NONE) {
        TimeUtil.toChatTime(timeStamp = timestamp)
    }

}

@Stable
sealed class Message(val messageDetail: MessageDetail) {

    abstract val formatMessage: String

    var tag: Any? = null

}

@Stable
data class TextMessage(
    private val detail: MessageDetail,
    private val text: String
) : Message(messageDetail = detail) {

    override val formatMessage: String
        get() = text

}

@Stable
data class SystemMessage(
    private val detail: MessageDetail,
    private val tips: String
) : Message(messageDetail = detail) {

    override val formatMessage: String
        get() = tips

}

@Stable
data class ImageMessage(
    private val detail: MessageDetail,
    private val original: ImageElement,
    private val large: ImageElement?,
    private val thumb: ImageElement?,
) : Message(messageDetail = detail) {

    override val formatMessage: String
        get() = "[图片]"

    val previewImage: ImageElement
        get() = large ?: original

    val previewImageUrl: String
        get() = previewImage.url

    val widgetWidthDp = 180f

    val widgetHeightDp = if (previewImage.width <= 0f || previewImage.height <= 0f) {
        widgetWidthDp
    } else {
        widgetWidthDp * (minOf(1.9f, 1.0f * previewImage.height / previewImage.width))
    }

}

@Stable
class ImageElement(
    val width: Int,
    val height: Int,
    val url: String
)

@Stable
class TimeMessage(targetMessage: Message) : Message(
    messageDetail = MessageDetail(
        msgId = (targetMessage.messageDetail.timestamp + targetMessage.messageDetail.msgId.hashCode()).toString(),
        timestamp = targetMessage.messageDetail.timestamp,
        state = MessageState.Completed,
        sender = PersonProfile.Empty,
        isOwnMessage = false
    )
) {

    override val formatMessage: String
        get() = messageDetail.chatTime

}

@Stable
sealed class LoadMessageResult {

    data class Success(val messageList: List<Message>, val loadFinish: Boolean) :
        LoadMessageResult()

    data class Failed(val reason: String) : LoadMessageResult()

}