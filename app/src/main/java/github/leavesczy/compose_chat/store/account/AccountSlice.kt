package github.leavesczy.compose_chat.base.store.account

import com.example.reduxforandroid.redux.Reducer
import com.example.reduxforandroid.redux.middleware.Thunk
import github.leavesczy.compose_chat.base.models.ActionResult
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.logic.ComposeChat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

data class AccountState(
    val lastLoginUserId: String = "",
    val autoLogin: Boolean = false,
    val personProfile: PersonProfile = PersonProfile.Empty
)

data class LoginSuccess(val lastLoginUserId: String)
class UserLogout()
data class UpdatePersonProfile(val personProfile: PersonProfile)


val loginReducer: Reducer<AccountState> = { state, action ->
    when (action) {
        is LoginSuccess -> state.copy(
            lastLoginUserId = action.lastLoginUserId,
            autoLogin = true
        )
        is UserLogout -> state.copy(
            autoLogin = false
        )
        is UpdatePersonProfile -> state.copy(
            personProfile = action.personProfile
        )
        else -> state
    }
}


fun getPersonProfile(viewModelScope: CoroutineScope): Thunk<AccountState> = { dispatch, getState, extraArg ->
    viewModelScope.launch {
        val profile = ComposeChat.accountProvider.getPersonProfile() ?: PersonProfile.Empty
        withContext(Dispatchers.Main) {
            dispatch(UpdatePersonProfile(profile))
        }
    }
}

fun updatePersonProfile(personProfile:PersonProfile,viewModelScope: CoroutineScope): Thunk<AccountState> = { dispatch, getState, extraArg ->
    viewModelScope.launch {
        val result = ComposeChat.accountProvider.updatePersonProfile(
            faceUrl = personProfile.faceUrl,
            nickname = personProfile.nickname,
            signature = personProfile.signature
        )
        when (result) {
            is ActionResult.Success -> {
                dispatch(UpdatePersonProfile(personProfile))
                ToastProvider.showToast(msg = "更新成功")
            }

            is ActionResult.Failed -> {
                ToastProvider.showToast(msg = result.reason)
            }
        }
    }
}




val refreshPersonProfile = ::getPersonProfile

