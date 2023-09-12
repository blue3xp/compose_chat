package github.leavesczy.compose_chat.ui.chat

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ChainStyle
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.base.model.ActionResult
import github.leavesczy.compose_chat.base.model.GroupMemberProfile
import github.leavesczy.compose_chat.base.model.GroupProfile
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.extend.scrim
import github.leavesczy.compose_chat.ui.MainActivity
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.chat.logic.GroupProfilePageAction
import github.leavesczy.compose_chat.ui.chat.logic.GroupProfilePageViewState
import github.leavesczy.compose_chat.ui.chat.logic.GroupProfileViewModel
import github.leavesczy.compose_chat.ui.friend.FriendProfileActivity
import github.leavesczy.compose_chat.ui.preview.PreviewImageActivity
import github.leavesczy.compose_chat.ui.widgets.BouncyImage
import github.leavesczy.compose_chat.ui.widgets.CoilImage
import github.leavesczy.compose_chat.ui.widgets.LoadingDialog
import github.leavesczy.compose_chat.utils.randomFaceUrl
import kotlinx.coroutines.launch
import kotlin.math.abs

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class GroupProfileActivity : BaseActivity() {

    companion object {

        private const val keyGroupId = "keyGroupId"

        fun navTo(context: Context, groupId: String) {
            val intent = Intent(context, GroupProfileActivity::class.java)
            intent.putExtra(keyGroupId, groupId)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val groupId by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getStringExtra(keyGroupId) ?: ""
    }

    private val groupProfileViewModel by viewModelsInstance {
        GroupProfileViewModel(groupId = groupId)
    }

    private val groupProfilePageAction = GroupProfilePageAction(
        setAvatar = {
            groupProfileViewModel.setAvatar(avatarUrl = it)
        },
        quitGroup = {
            lifecycleScope.launch {
                when (val result = groupProfileViewModel.quitGroup()) {
                    ActionResult.Success -> {
                        showToast(msg = "已退出群聊")
                        startActivity(Intent(this@GroupProfileActivity, MainActivity::class.java))
                    }

                    is ActionResult.Failed -> {
                        showToast(msg = result.reason)
                    }
                }
            }
        },
        onClickMember = {
            FriendProfileActivity.navTo(
                context = this,
                friendId = it.detail.id
            )
        }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewState = groupProfileViewModel.pageViewState
            if (viewState != null) {
                GroupProfilePage(
                    viewState = viewState,
                    action = groupProfilePageAction
                )
            }
            LoadingDialog(visible = groupProfileViewModel.loadingDialogVisible)
        }
    }

}

private val groupHeaderHeight = 280.dp

private val groupTopBarHeight = 44.dp

@Composable
private fun GroupProfilePage(
    viewState: GroupProfilePageViewState,
    action: GroupProfilePageAction
) {
    val listState = rememberLazyListState()
    val maxOffsetHeightPx = with(LocalDensity.current) {
        (groupHeaderHeight - groupTopBarHeight - 30.dp).roundToPx()
    }
    val topBarAlpha by remember {
        derivedStateOf {
            val visibleItemsInfo = listState.layoutInfo.visibleItemsInfo
            if (visibleItemsInfo.isEmpty()) {
                0f
            } else {
                val first = visibleItemsInfo.first()
                if (first.index == 0) {
                    val offset = abs(first.offset).coerceIn(0, maxOffsetHeightPx)
                    1.0f * offset / maxOffsetHeightPx
                } else {
                    1.0f
                }
            }
        }
    }
    Scaffold(
        modifier = Modifier.fillMaxSize(),
        contentWindowInsets = WindowInsets.navigationBars
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = listState,
                contentPadding = PaddingValues(bottom = 80.dp),
            ) {
                item(key = "header") {
                    GroupHeader(groupProfile = viewState.groupProfile)
                }
                items(
                    items = viewState.memberList,
                    key = {
                        it.detail.id
                    },
                    itemContent = {
                        GroupMemberItem(
                            groupMemberProfile = it,
                            groupProfilePageAction = action
                        )
                    }
                )
            }
            PageTopBar(
                title = viewState.groupProfile.name,
                alpha = topBarAlpha,
                action = action
            )
        }
    }
}

@Composable
private fun GroupHeader(groupProfile: GroupProfile) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(height = groupHeaderHeight)
    ) {
        val context = LocalContext.current
        val avatarUrl = groupProfile.faceUrl
        val introduction =
            "GroupID: ${groupProfile.id}\nCreateTime: ${groupProfile.createTimeFormat}\nMemberCount: ${groupProfile.memberCount}"
        CoilImage(
            modifier = Modifier
                .fillMaxSize()
                .scrim(color = Color(0x33000000)),
            data = avatarUrl
        )
        Column(
            modifier = Modifier
                .align(alignment = Alignment.TopCenter)
                .statusBarsPadding()
                .padding(top = 20.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            BouncyImage(
                modifier = Modifier
                    .statusBarsPadding()
                    .size(size = 100.dp)
                    .clickableNoRipple {
                        if (avatarUrl.isNotBlank()) {
                            PreviewImageActivity.navTo(context = context, imageUrl = avatarUrl)
                        }
                    },
                data = avatarUrl
            )
            Text(
                modifier = Modifier
                    .padding(all = 14.dp),
                text = introduction,
                fontSize = 15.sp,
                textAlign = TextAlign.Center,
                color = Color.White
            )
        }
    }
}

@Composable
private fun PageTopBar(
    title: String,
    alpha: Float,
    action: GroupProfilePageAction
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    Box(
        modifier = Modifier
            .alpha(alpha = alpha)
            .fillMaxWidth()
            .background(color = MaterialTheme.colorScheme.background)
            .statusBarsPadding()
            .height(height = groupTopBarHeight)
    ) {
        Text(
            modifier = Modifier
                .align(alignment = Alignment.Center),
            text = title,
            color = MaterialTheme.colorScheme.onBackground,
            fontSize = 19.sp,
        )
        Icon(
            modifier = Modifier
                .align(alignment = Alignment.CenterEnd)
                .padding(end = 12.dp)
                .size(size = 28.dp)
                .clickable {
                    menuExpanded = true
                },
            imageVector = Icons.Default.MoreVert,
            contentDescription = null
        )
    }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .wrapContentSize(align = Alignment.TopEnd)
            .padding(end = 20.dp)
    ) {
        DropdownMenu(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.background),
            expanded = menuExpanded,
            onDismissRequest = {
                menuExpanded = false
            }
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "修改头像",
                        style = TextStyle(fontSize = 18.sp)
                    )
                },
                onClick = {
                    menuExpanded = false
                    action.setAvatar(randomFaceUrl())
                }
            )
            DropdownMenuItem(
                text = {
                    Text(
                        text = "退出群聊",
                        style = TextStyle(fontSize = 18.sp)
                    )
                },
                onClick = {
                    menuExpanded = false
                    action.quitGroup()
                }
            )
        }
    }
}

@Composable
private fun GroupMemberItem(
    groupMemberProfile: GroupMemberProfile,
    groupProfilePageAction: GroupProfilePageAction
) {
    ConstraintLayout(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                groupProfilePageAction.onClickMember(groupMemberProfile)
            },
    ) {
        val (avatarRef, showNameRef, roleRef, dividerRef) = createRefs()
        val verticalChain =
            createVerticalChain(showNameRef, roleRef, chainStyle = ChainStyle.Packed)
        constrain(ref = verticalChain) {
            top.linkTo(anchor = parent.top)
            bottom.linkTo(anchor = parent.bottom)
        }
        CoilImage(
            modifier = Modifier
                .constrainAs(ref = avatarRef) {
                    start.linkTo(anchor = parent.start)
                    linkTo(top = parent.top, bottom = parent.bottom)
                }
                .padding(start = 14.dp, top = 8.dp, bottom = 8.dp)
                .size(size = 50.dp)
                .clip(shape = RoundedCornerShape(size = 6.dp)),
            data = groupMemberProfile.detail.faceUrl)
        Text(
            modifier = Modifier
                .constrainAs(ref = showNameRef) {
                    linkTo(
                        start = avatarRef.end,
                        end = parent.end,
                        startMargin = 12.dp,
                        endMargin = 12.dp
                    )
                    width = Dimension.fillToConstraints
                }
                .padding(bottom = 1.dp),
            text = groupMemberProfile.detail.showName + "（${
                groupMemberProfile.detail.id + if (groupMemberProfile.isOwner) {
                    " - 群主"
                } else {
                    ""
                }
            }）",
            fontSize = 17.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Text(
            modifier = Modifier
                .constrainAs(ref = roleRef) {
                    linkTo(start = showNameRef.start, end = parent.end, endMargin = 12.dp)
                    width = Dimension.fillToConstraints
                }
                .padding(top = 1.dp),
            text = "joinTime: ${groupMemberProfile.joinTimeFormat}",
            fontSize = 14.sp,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1
        )
        Divider(
            modifier = Modifier.constrainAs(ref = dividerRef) {
                linkTo(
                    start = avatarRef.end, end = parent.end
                )
                bottom.linkTo(anchor = parent.bottom)
                width = Dimension.fillToConstraints
            },
            thickness = 0.2.dp
        )
    }
}