package github.leavesczy.compose_chat.ui.login.logic

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.store.account.LoginSuccess
import github.leavesczy.compose_chat.store.store
import github.leavesczy.compose_chat.ui.base.BaseViewModel
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import kotlinx.coroutines.delay

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class LoginViewModel : BaseViewModel() {

    val loginPageViewState by mutableStateOf(
        value = LoginPageViewState(
            lastLoginUserId = mutableStateOf(value = ""),
            showPanel = mutableStateOf(value = false),
            loading = mutableStateOf(value = false)
        )
    )

    suspend fun tryLogin(): Boolean {
//        val lastLoginUserId = AccountProvider.lastLoginUserId
        val lastLoginUserId = store.state.accountState.lastLoginUserId;
//            return if (lastLoginUserId.isBlank() || !AccountProvider.canAutoLogin) {
        return if (lastLoginUserId.isBlank() || !store.state.accountState.autoLogin) {
            loginPageViewState.lastLoginUserId.value = lastLoginUserId
            loginPageViewState.showPanel.value = true
            loginPageViewState.loading.value = false
            false
        } else {
            loginPageViewState.lastLoginUserId.value = lastLoginUserId
            loginPageViewState.showPanel.value = false
            loginPageViewState.loading.value = true
            login(userId = lastLoginUserId)
        }
    }

    suspend fun onClickLoginButton(userId: String): Boolean {
        loginPageViewState.lastLoginUserId.value = userId
        loginPageViewState.loading.value = true
        return login(userId = userId)
    }

    private suspend fun login(userId: String): Boolean {
        val formatUserId = userId.lowercase()
        return when (val loginResult = ComposeChat.accountProvider.login(userId = formatUserId)) {
            is ActionResult.Success -> {
//                AccountProvider.onUserLogin(userId = formatUserId)
                store.dispatch(LoginSuccess(formatUserId))
                delay(timeMillis = 250)
                true
            }

            is ActionResult.Failed -> {
                showToast(msg = loginResult.reason)
                loginPageViewState.lastLoginUserId.value = formatUserId
                loginPageViewState.showPanel.value = true
                loginPageViewState.loading.value = false
                false
            }
        }
    }

}