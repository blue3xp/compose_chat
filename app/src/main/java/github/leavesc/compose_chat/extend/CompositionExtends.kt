package github.leavesc.compose_chat.extend

import android.content.Context
import android.view.inputmethod.InputMethodManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController

/**
 * @Author: leavesC
 * @Date: 2021/10/21 11:57
 * @Desc:
 * @Github：https://github.com/leavesC
 */
val LocalInputMethodManager = staticCompositionLocalOf<InputMethodManager> {
    error("CompositionLocal InputMethodManager not present")
}

@Composable
fun ProvideInputMethodManager(content: @Composable () -> Unit) {
    val context = LocalContext.current
    val inputMethodManager = remember {
        context.getSystemService(
            Context.INPUT_METHOD_SERVICE
        ) as InputMethodManager
    }
    CompositionLocalProvider(LocalInputMethodManager provides inputMethodManager) {
        content()
    }
}

val LocalNavHostController = staticCompositionLocalOf<NavHostController> {
    error("CompositionLocal NavHostController not present")
}

@Composable
fun ProvideNavHostController(
    navHostController: NavHostController,
    content: @Composable () -> Unit
) {
    CompositionLocalProvider(LocalNavHostController provides navHostController) {
        content()
    }
}