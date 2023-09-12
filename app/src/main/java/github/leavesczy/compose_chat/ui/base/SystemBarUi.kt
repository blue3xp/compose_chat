package github.leavesczy.compose_chat.ui.base

import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.logic.AppTheme

/**
 * @Author: leavesCZY
 * @Desc:
 */
fun ComponentActivity.setSystemBarUi(
    statusBarColor: Int,
    navigationBarColor: Int,
    statusBarDarkIcons: Boolean,
    navigationBarDarkIcons: Boolean
) {
    val statusBarStyle = if (statusBarDarkIcons) {
        SystemBarStyle.light(
            scrim = statusBarColor,
            darkScrim = statusBarColor
        )
    } else {
        SystemBarStyle.dark(
            scrim = statusBarColor
        )
    }
    val navigationBarStyle = if (navigationBarDarkIcons) {
        SystemBarStyle.light(
            scrim = navigationBarColor,
            darkScrim = navigationBarColor
        )
    } else {
        SystemBarStyle.dark(
            scrim = navigationBarColor
        )
    }
    enableEdgeToEdge(
        statusBarStyle = statusBarStyle,
        navigationBarStyle = navigationBarStyle
    )
}

fun ComponentActivity.setSystemBarUi(appTheme: AppTheme = AppThemeProvider.appTheme) {
    val statusBarDarkIcons: Boolean
    val navigationBarDarkIcons: Boolean
    when (appTheme) {
        AppTheme.Light, AppTheme.Gray -> {
            statusBarDarkIcons = true
            navigationBarDarkIcons = true
        }

        AppTheme.Dark -> {
            statusBarDarkIcons = false
            navigationBarDarkIcons = false
        }
    }
    setSystemBarUi(
        statusBarColor = android.graphics.Color.TRANSPARENT,
        navigationBarColor = android.graphics.Color.TRANSPARENT,
        statusBarDarkIcons = statusBarDarkIcons,
        navigationBarDarkIcons = navigationBarDarkIcons
    )
}

@Composable
fun SetSystemBarUi(
    statusBarColor: Color,
    navigationBarColor: Color,
    statusBarDarkIcons: Boolean,
    navigationBarDarkIcons: Boolean
) {
    val context = LocalContext.current
    LaunchedEffect(statusBarColor, navigationBarColor, statusBarDarkIcons, navigationBarDarkIcons) {
        if (context is ComponentActivity) {
            context.setSystemBarUi(
                statusBarColor = statusBarColor.toArgb(),
                navigationBarColor = navigationBarColor.toArgb(),
                statusBarDarkIcons = statusBarDarkIcons,
                navigationBarDarkIcons = navigationBarDarkIcons
            )
        }
    }
}

@Composable
fun SetSystemBarUi(appTheme: AppTheme = AppThemeProvider.appTheme) {
    val context = LocalContext.current
    LaunchedEffect(appTheme) {
        if (context is ComponentActivity) {
            val statusBarDarkIcons: Boolean
            val navigationBarDarkIcons: Boolean
            when (appTheme) {
                AppTheme.Light, AppTheme.Gray -> {
                    statusBarDarkIcons = true
                    navigationBarDarkIcons = true
                }

                AppTheme.Dark -> {
                    statusBarDarkIcons = false
                    navigationBarDarkIcons = false
                }
            }
            context.setSystemBarUi(
                statusBarColor = android.graphics.Color.TRANSPARENT,
                navigationBarColor = android.graphics.Color.TRANSPARENT,
                statusBarDarkIcons = statusBarDarkIcons,
                navigationBarDarkIcons = navigationBarDarkIcons
            )
        }
    }
}