package github.leavesc.compose_chat.ui.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import com.google.accompanist.insets.statusBarsPadding
import github.leavesc.compose_chat.base.model.ActionResult
import github.leavesc.compose_chat.base.model.GroupMemberProfile
import github.leavesc.compose_chat.extend.LocalNavHostController
import github.leavesc.compose_chat.extend.navToHomeScreen
import github.leavesc.compose_chat.extend.viewModelInstance
import github.leavesc.compose_chat.logic.GroupProfileViewModel
import github.leavesc.compose_chat.model.Screen
import github.leavesc.compose_chat.ui.home.randomFaceUrl
import github.leavesc.compose_chat.ui.profile.ProfileScreen
import github.leavesc.compose_chat.ui.weigets.CoilCircleImage
import github.leavesc.compose_chat.ui.weigets.CommonDivider
import github.leavesc.compose_chat.utils.showToast
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/10/27 18:04
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun GroupProfileScreen(groupId: String) {
    val groupProfileViewModel = viewModelInstance {
        GroupProfileViewModel(groupId = groupId)
    }
    val groupProfileScreenState by groupProfileViewModel.groupProfileScreenState.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val navHostController = LocalNavHostController.current
    Scaffold(
        modifier = Modifier
            .fillMaxSize(),
    ) {
        Box {
            val onClickMember: (GroupMemberProfile) -> Unit = remember {
                object : (GroupMemberProfile) -> Unit {
                    override fun invoke(member: GroupMemberProfile) {
                        navHostController.navigate(
                            route = Screen.FriendProfileScreen.generateRoute(friendId = member.detail.userId)
                        )
                    }
                }
            }
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize(),
            ) {
                item(key = true) {
                    ProfileScreen(groupProfile = groupProfileScreenState.groupProfile)
                }
                val memberList = groupProfileScreenState.memberList
                memberList.forEach {
                    item(key = it.detail.userId) {
                        GroupMemberItem(groupMemberProfile = it, onClickMember = onClickMember)
                    }
                }
                item {
                    Spacer(modifier = Modifier.height(height = 40.dp))
                }
            }
            GroupProfileScreenTopBar(
                randomAvatar = {
                    groupProfileViewModel.setAvatar(avatarUrl = randomFaceUrl())
                },
                quitGroup = {
                    coroutineScope.launch {
                        when (val result = groupProfileViewModel.quitGroup()) {
                            ActionResult.Success -> {
                                showToast("已退出群聊")
                                navHostController.navToHomeScreen()
                            }
                            is ActionResult.Failed -> {
                                showToast(result.reason)
                            }
                        }
                    }
                })
        }
    }
}

@Composable
private fun GroupMemberItem(
    groupMemberProfile: GroupMemberProfile,
    onClickMember: (GroupMemberProfile) -> Unit
) {
    val padding = 12.dp
    ConstraintLayout(
        modifier = Modifier
            .zIndex(zIndex = 10f)
            .fillMaxWidth()
            .clickable {
                onClickMember(groupMemberProfile)
            },
    ) {
        val (avatar, showName, role, divider) = createRefs()
        CoilCircleImage(
            data = groupMemberProfile.detail.faceUrl,
            modifier = Modifier
                .padding(start = padding * 1.5f, top = padding, bottom = padding)
                .size(size = 50.dp)
                .constrainAs(ref = avatar) {
                    start.linkTo(anchor = parent.start)
                    top.linkTo(anchor = parent.top)
                }
        )
        Text(
            text = groupMemberProfile.detail.showName + "（ID: ${
                groupMemberProfile.detail.userId + if (groupMemberProfile.isOwner) {
                    " - 群主"
                } else {
                    ""
                }
            }）",
            style = MaterialTheme.typography.subtitle1,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, top = padding, end = padding)
                .constrainAs(ref = showName) {
                    start.linkTo(anchor = avatar.end)
                    top.linkTo(anchor = parent.top)
                    end.linkTo(anchor = parent.end)
                    width = Dimension.fillToConstraints
                }
        )
        Text(
            text = "joinTime: ${groupMemberProfile.joinTimeFormat}",
            style = MaterialTheme.typography.body2,
            overflow = TextOverflow.Ellipsis,
            maxLines = 1,
            modifier = Modifier
                .padding(start = padding, end = padding)
                .constrainAs(ref = role) {
                    start.linkTo(anchor = showName.start)
                    top.linkTo(anchor = showName.bottom, margin = padding / 2)
                    end.linkTo(anchor = parent.end)
                    width = Dimension.fillToConstraints
                }
        )
        CommonDivider(
            modifier = Modifier
                .constrainAs(ref = divider) {
                    start.linkTo(anchor = avatar.end, margin = padding)
                    end.linkTo(anchor = parent.end)
                    top.linkTo(anchor = avatar.bottom)
                    width = Dimension.fillToConstraints
                }
        )
    }
}

@Composable
private fun GroupProfileScreenTopBar(
    randomAvatar: () -> Unit,
    quitGroup: () -> Unit,
) {
    var menuExpanded by remember {
        mutableStateOf(false)
    }
    TopAppBar(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding(),
        backgroundColor = Color.Transparent,
        contentColor = Color.Transparent,
        elevation = 0.dp,
        contentPadding = PaddingValues(
            all = 0.dp
        ),
    ) {
        ConstraintLayout(
            modifier = Modifier
                .fillMaxSize()
        ) {
            val (menuIcon, menu) = createRefs()
            Icon(
                modifier = Modifier
                    .constrainAs(ref = menuIcon) {
                        end.linkTo(anchor = parent.end)
                        top.linkTo(anchor = parent.top)
                        bottom.linkTo(anchor = parent.bottom)
                    }
                    .padding(end = 12.dp)
                    .size(size = 28.dp)
                    .clickable {
                        menuExpanded = true
                    },
                imageVector = Icons.Default.MoreVert,
                contentDescription = null,
                tint = MaterialTheme.colors.surface
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentSize(align = Alignment.TopEnd)
                    .padding(end = 20.dp)
                    .constrainAs(ref = menu) {
                        top.linkTo(anchor = menuIcon.bottom)
                    }
            ) {
                DropdownMenu(
                    modifier = Modifier.background(color = MaterialTheme.colors.background),
                    expanded = menuExpanded,
                    onDismissRequest = {
                        menuExpanded = false
                    }
                ) {
                    DropdownMenuItem(onClick = {
                        menuExpanded = false
                        randomAvatar()
                    }) {
                        Text(text = "修改头像", modifier = Modifier)
                    }
                    DropdownMenuItem(onClick = {
                        menuExpanded = false
                        quitGroup()
                    }) {
                        Text(text = "退出群聊", modifier = Modifier)
                    }
                }
            }
        }
    }
}