package github.leavesczy.compose_chat.store

import com.example.reduxforandroid.redux.Reducer
import com.example.reduxforandroid.redux.applyMiddleware
import com.example.reduxforandroid.redux.createStore
import com.example.reduxforandroid.redux.middleware.createLoggerMiddleware
import com.example.reduxforandroid.redux.middleware.createThunkMiddleware
import github.leavesczy.compose_chat.base.store.account.AccountState
import github.leavesczy.compose_chat.base.store.account.loginReducer
import github.leavesczy.compose_chat.store.chat.ChatState
import github.leavesczy.compose_chat.store.chat.chatReducer

/**
 * Entire state tree for the app.
 */
data class AppState(
    val accountState: AccountState = AccountState(),
    val chatState: ChatState = ChatState()
)

val appReducer: Reducer<AppState> = { state, action ->
    AppState(
        accountState = loginReducer(state.accountState, action),
        chatState = chatReducer(state.chatState, action),
    )
}


val store = createStore(
    appReducer, AppState(),
    applyMiddleware(createThunkMiddleware(), createLoggerMiddleware())
);

