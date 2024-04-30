package github.leavesczy.compose_chat.base.store.account

import com.example.reduxforandroid.redux.Reducer
import com.example.reduxforandroid.redux.applyMiddleware
import com.example.reduxforandroid.redux.createStore
import com.example.reduxforandroid.redux.middleware.createLoggerMiddleware
import com.example.reduxforandroid.redux.middleware.createThunkMiddleware
import github.leavesczy.compose_chat.base.provider.IAccountProvider

/**
 * Entire state tree for the app.
 */
data class AppState(
    val accountState: AccountState = AccountState()
)

val appReducer: Reducer<AppState> = { state, action ->
    AppState(
        accountState = loginReducer(state.accountState, action),
    )
}


val store = createStore(
    appReducer, AppState(),
    applyMiddleware(createThunkMiddleware(), createLoggerMiddleware())
);

