package github.leavesc.compose_chat.ui.friend

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import github.leavesc.compose_chat.base.model.PersonProfile
import github.leavesc.compose_chat.extend.LocalNavHostController
import github.leavesc.compose_chat.extend.navToC2CChatScreen
import github.leavesc.compose_chat.extend.navToHomeScreen
import github.leavesc.compose_chat.extend.viewModelInstance
import github.leavesc.compose_chat.logic.FriendProfileViewModel
import github.leavesc.compose_chat.ui.profile.ProfileScreen
import github.leavesc.compose_chat.ui.theme.BottomSheetShape
import github.leavesc.compose_chat.ui.weigets.CommonButton
import github.leavesc.compose_chat.ui.weigets.CommonOutlinedTextField
import github.leavesc.compose_chat.ui.weigets.CommonSnackbar
import kotlinx.coroutines.launch

/**
 * @Author: leavesC
 * @Date: 2021/7/4 1:01
 * @Desc:
 * @Github：https://github.com/leavesC
 */
@Composable
fun FriendProfileScreen(
    friendId: String
) {
    val friendProfileViewModel = viewModelInstance {
        FriendProfileViewModel(friendId = friendId)
    }
    LaunchedEffect(key1 = Unit) {
        friendProfileViewModel.getFriendProfile()
    }
    val friendProfile by friendProfileViewModel.friendProfile.collectAsState()
    val coroutineScope = rememberCoroutineScope()
    val sheetState = rememberModalBottomSheetState(ModalBottomSheetValue.Hidden)
    val navHostController = LocalNavHostController.current

    fun expandSheetContent() {
        coroutineScope.launch {
            sheetState.animateTo(targetValue = ModalBottomSheetValue.Expanded)
        }
    }

    ModalBottomSheetLayout(
        sheetState = sheetState,
        sheetShape = BottomSheetShape,
        sheetContent = {
            SetFriendRemarkScreen(friendProfile = friendProfile,
                modalBottomSheetState = sheetState,
                onSetRemark = { friendId, remark ->
                    friendProfileViewModel.setFriendRemark(
                        friendId = friendId,
                        remark = remark
                    )
                })
        }
    ) {
        var openDeleteFriendDialog by remember { mutableStateOf(false) }
        Scaffold(modifier = Modifier.fillMaxSize()) {
            ProfileScreen(
                personProfile = friendProfile
            ) {
                if (friendProfile.isFriend) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CommonButton(text = "去聊天吧") {
                            navHostController.popBackStack()
                            navHostController.navToC2CChatScreen(friendId = friendProfile.userId)
                        }
                        CommonButton(text = "设置备注") {
                            expandSheetContent()
                        }
                        CommonButton(text = "删除好友") {
                            openDeleteFriendDialog = true
                        }
                    }
                }
            }
            if (openDeleteFriendDialog) {
                DeleteFriendDialog(friendProfile = friendProfile, onDeleteFriend = {
                    friendProfileViewModel.deleteFriend(friendId = it)
                    navHostController.navToHomeScreen()
                }, onDismissRequest = {
                    openDeleteFriendDialog = false
                })
            }
        }
    }
}

@Composable
private fun DeleteFriendDialog(
    friendProfile: PersonProfile,
    onDeleteFriend: (String) -> Unit,
    onDismissRequest: () -> Unit
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .padding(all = 20.dp),
        title = {
            Text(text = "确认删除好友吗？")
        },
        confirmButton = {
            Text(
                modifier = Modifier
                    .padding(all = 20.dp)
                    .clickable {
                        onDismissRequest()
                        onDeleteFriend(friendProfile.userId)
                    },
                text = "删除",
            )
        },
        dismissButton = {
            Text(
                modifier = Modifier
                    .padding(all = 20.dp)
                    .clickable {
                        onDismissRequest()
                    },
                text = "取消"
            )
        },
        onDismissRequest = {
            onDismissRequest()
        },
    )
}

@Composable
private fun SetFriendRemarkScreen(
    friendProfile: PersonProfile,
    modalBottomSheetState: ModalBottomSheetState,
    onSetRemark: (userId: String, remark: String) -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val scaffoldState = rememberScaffoldState()

    fun expandSheetContent(targetValue: ModalBottomSheetValue) {
        coroutineScope.launch {
            modalBottomSheetState.animateTo(targetValue = targetValue)
        }
    }

    BackHandler(enabled = modalBottomSheetState.isVisible, onBack = {
        when (modalBottomSheetState.currentValue) {
            ModalBottomSheetValue.Hidden -> {

            }
            ModalBottomSheetValue.Expanded -> {
                expandSheetContent(targetValue = ModalBottomSheetValue.HalfExpanded)
            }
            ModalBottomSheetValue.HalfExpanded -> {
                expandSheetContent(targetValue = ModalBottomSheetValue.Hidden)
            }
        }
    })

    var remark by remember(key1 = friendProfile) {
        mutableStateOf(
            friendProfile.remark
        )
    }
    Scaffold(
        modifier = Modifier
            .fillMaxWidth()
            .fillMaxHeight(fraction = 0.8f),
        scaffoldState = scaffoldState,
        snackbarHost = {
            CommonSnackbar(it)
        },
    ) {
        Column {
            CommonOutlinedTextField(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .padding(
                        horizontal = 20.dp,
                        vertical = 10.dp
                    ),
                value = remark,
                onValueChange = {
                    remark = it
                },
                label = "Set Remark",
            )
            CommonButton(text = "设置备注") {
                onSetRemark(friendProfile.userId, remark)
            }
        }
    }
}