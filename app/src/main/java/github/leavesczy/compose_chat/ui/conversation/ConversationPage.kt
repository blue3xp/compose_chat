package github.leavesczy.compose_chat.ui.conversation

import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import github.leavesczy.compose_chat.base.models.Conversation
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.conversation.logic.ConversationPageViewState
import github.leavesczy.compose_chat.ui.conversation.logic.ConversationViewModel
import github.leavesczy.compose_chat.ui.widgets.ComponentImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun ConversationPage() {
    val conversationViewModel = viewModel<ConversationViewModel>()
    ConversationPage(pageViewState = conversationViewModel.pageViewState)
}

@Composable
private fun ConversationPage(pageViewState: ConversationPageViewState) {
    val conversationList = pageViewState.conversationList
    if (conversationList.isEmpty()) {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(fraction = 0.45f)
                .wrapContentSize(align = Alignment.BottomCenter),
            text = "Empty",
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            fontSize = 70.sp
        )
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            state = pageViewState.listState,
            contentPadding = PaddingValues(bottom = 60.dp),
        ) {
            items(
                items = conversationList,
                key = {
                    it.id
                },
                contentType = {
                    "Conversation"
                }
            ) {
                ConversationItem(
                    conversation = it,
                    pageViewState = pageViewState
                )
            }
        }
    }
}

@Composable
private fun LazyItemScope.ConversationItem(
    conversation: Conversation,
    pageViewState: ConversationPageViewState
) {
    var offset = Offset.Zero
    var menuExpanded by remember {
        mutableStateOf(value = false)
    }
    Row(
        modifier = Modifier
            .animateItemPlacement()
            .then(
                other = if (conversation.isPinned) {
                    Modifier.scrim(color = Color(0x26CCCCCC))
                } else {
                    Modifier
                }
            )
            .fillMaxWidth()
            .height(height = 70.dp)
            .pointerInteropFilter {
                offset = Offset(it.x / 2, -it.y)
                false
            }
            .combinedClickable(
                onClick = {
                    pageViewState.onClickConversation(conversation)
                },
                onLongClick = {
                    menuExpanded = true
                }
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier.fillMaxHeight()
        ) {
            ComponentImage(
                modifier = Modifier
                    .align(alignment = Alignment.Center)
                    .padding(horizontal = 12.dp)
                    .size(size = 50.dp)
                    .clip(shape = RoundedCornerShape(size = 6.dp)),
                model = conversation.faceUrl
            )
            val unreadMessageCount = conversation.unreadMessageCount
            if (unreadMessageCount > 0) {
                val count = if (unreadMessageCount > 99) {
                    "99+"
                } else {
                    unreadMessageCount.toString()
                }
                Text(
                    modifier = Modifier
                        .align(alignment = Alignment.TopEnd)
                        .padding(top = 2.dp)
                        .size(size = 20.dp)
                        .background(color = MaterialTheme.colorScheme.primary, shape = CircleShape)
                        .wrapContentSize(align = Alignment.Center),
                    text = count,
                    color = Color.White,
                    fontSize = 12.sp,
                    textAlign = TextAlign.Center
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(weight = 1f)
                .fillMaxHeight()
                .padding(end = 12.dp),
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f)
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    modifier = Modifier
                        .weight(weight = 1f),
                    text = conversation.name,
                    fontSize = 17.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier,
                    text = conversation.lastMessage.messageDetail.conversationTime,
                    fontSize = 12.sp
                )
            }
            Text(
                modifier = Modifier
                    .padding(top = 1.dp),
                text = conversation.formatMsg,
                fontSize = 14.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f)
            )
            Divider(
                modifier = Modifier,
                thickness = 0.2.dp
            )
        }
        MoreActionDropdownMenu(
            modifier = Modifier,
            expanded = menuExpanded,
            offset = offset,
            conversation = conversation,
            onDismissRequest = {
                menuExpanded = false
            },
            deleteConversation = pageViewState.deleteConversation,
            pinConversation = pageViewState.pinConversation
        )
    }
}

@Composable
private fun MoreActionDropdownMenu(
    modifier: Modifier,
    expanded: Boolean,
    offset: Offset,
    onDismissRequest: () -> Unit,
    conversation: Conversation,
    deleteConversation: (Conversation) -> Unit,
    pinConversation: (Conversation, Boolean) -> Unit
) {
    val dpOffset = with(LocalDensity.current) {
        DpOffset(x = offset.x.toDp(), y = offset.y.toDp())
    }
    DropdownMenu(
        modifier = modifier.background(color = MaterialTheme.colorScheme.background),
        expanded = expanded,
        offset = dpOffset,
        onDismissRequest = onDismissRequest
    ) {
        DropdownMenuItem(
            modifier = Modifier,
            text = {
                Text(
                    text = if (conversation.isPinned) {
                        "取消置顶"
                    } else {
                        "置顶会话"
                    },
                    style = TextStyle(fontSize = 18.sp)
                )
            },
            onClick = {
                onDismissRequest()
                pinConversation(conversation, !conversation.isPinned)
            }
        )
        DropdownMenuItem(
            modifier = Modifier,
            text = {
                Text(
                    text = "删除会话",
                    style = TextStyle(fontSize = 18.sp)
                )
            },
            onClick = {
                onDismissRequest()
                deleteConversation(conversation)
            }
        )
    }
}