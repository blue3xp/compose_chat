package github.leavesczy.compose_chat.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.provider.AppThemeProvider
import github.leavesczy.compose_chat.ui.logic.AppTheme

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
val LightColorScheme = lightColorScheme(
    primary = primaryColorLight,
    onPrimary = onPrimaryColorLight,
    primaryContainer = primaryContainerColorLight,
    surface = surfaceColorLight,
    onSurface = onSurfaceColorLight,
    inverseSurface = inverseSurfaceColorLight,
    inverseOnSurface = inverseOnSurfaceColorLight,
    background = backgroundColorLight,
    onSecondaryContainer = onSecondaryContainerLight
)

val DarkColorScheme = darkColorScheme(
    primary = primaryColorDark,
    onPrimary = onPrimaryColorDark,
    primaryContainer = primaryContainerColorDark,
    surface = surfaceColorDark,
    onSurface = onSurfaceColorDark,
    inverseSurface = inverseSurfaceColorDark,
    inverseOnSurface = inverseOnSurfaceColorDark,
    background = backgroundColorDark,
    onSecondaryContainer = onSecondaryContainerDark
)

val WindowInsetsEmpty = WindowInsets(left = 0.dp, top = 0.dp, right = 0.dp, bottom = 0.dp)

private const val DESIGN_WIDTH = 380f

@Composable
fun ComposeChatTheme(content: @Composable () -> Unit) {
    val colorScheme = when (AppThemeProvider.appTheme) {
        AppTheme.Light, AppTheme.Gray -> {
            LightColorScheme
        }

        AppTheme.Dark -> {
            DarkColorScheme
        }
    }
    val context = LocalContext.current
    val rememberedDensity = remember {
        Density(
            density = context.resources.displayMetrics.widthPixels / DESIGN_WIDTH,
            fontScale = 1f
        )
    }
    CompositionLocalProvider(LocalDensity provides rememberedDensity) {
        MaterialTheme(
            colorScheme = colorScheme,
            typography = AppTypography,
            content = {
                content()
                if (AppThemeProvider.appTheme == AppTheme.Gray) {
                    Canvas(modifier = Modifier.fillMaxSize()) {
                        drawRect(
                            color = Color.LightGray,
                            blendMode = BlendMode.Saturation
                        )
                    }
                }
            }
        )
    }
}