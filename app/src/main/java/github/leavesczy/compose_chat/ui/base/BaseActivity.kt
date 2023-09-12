package github.leavesczy.compose_chat.ui.base

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.runtime.Composable
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.theme.ComposeChatTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
open class BaseActivity : AppCompatActivity() {

    protected inline fun <reified T : ViewModel> viewModelsInstance(crossinline create: () -> T): Lazy<T> {
        return viewModels(factoryProducer = {
            object : ViewModelProvider.NewInstanceFactory() {
                @Suppress("UNCHECKED_CAST")
                override fun <T : ViewModel> create(modelClass: Class<T>): T {
                    return create() as T
                }
            }
        })
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        setSystemBarUi()
        super.onCreate(savedInstanceState)
    }

    protected fun setContent(content: @Composable () -> Unit) {
        setContent(
            parent = null,
            content = {
                ComposeChatTheme {
                    content()
                }
            }
        )
    }

    protected open fun setSystemBarUi() {
        setSystemBarUi(appTheme = AppThemeProvider.appTheme)
    }

    protected fun showToast(msg: String) {
        ToastProvider.showToast(msg = msg)
    }

    protected inline fun <reified T : Activity> startActivity() {
        val intent = Intent(this, T::class.java)
        startActivity(intent)
    }

}