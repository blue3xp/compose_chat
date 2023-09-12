package github.leavesczy.compose_chat.ui

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.animateIntOffsetAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Cabin
import androidx.compose.material.icons.filled.ColorLens
import androidx.compose.material.icons.filled.Sailing
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.contentColorFor
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.BuildConfig
import github.leavesczy.compose_chat.extend.clickableNoRipple
import github.leavesczy.compose_chat.ui.logic.MainPageDrawerViewState
import github.leavesczy.compose_chat.ui.widgets.BouncyImage
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.random.Random

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun MainPageDrawer(viewState: MainPageDrawerViewState) {
    val coroutineScope = rememberCoroutineScope()
    BackHandler(enabled = viewState.drawerState.isOpen) {
        coroutineScope.launch {
            viewState.drawerState.close()
        }
    }
    Surface(
        modifier = Modifier
            .fillMaxWidth(fraction = 0.85f)
            .background(color = MaterialTheme.colorScheme.surface)
            .padding(vertical = 22.dp)
            .statusBarsPadding()
            .navigationBarsPadding(),
        color = Color.Transparent,
        contentColor = contentColorFor(MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            val personProfile = viewState.personProfile
            val padding = 20.dp
            var animated by remember {
                mutableStateOf(value = false)
            }
            LaunchedEffect(key1 = viewState.drawerState.isOpen) {
                val isOpen = viewState.drawerState.isOpen
                if (isOpen) {
                    animated = true
                    delay(timeMillis = 1000)
                }
                animated = false
            }
            AnimateBouncyImage(
                modifier = Modifier
                    .padding(start = padding)
                    .size(size = 90.dp)
                    .clickableNoRipple {
                        viewState.previewImage(personProfile.faceUrl)
                    },
                faceUrl = personProfile.faceUrl,
                animated = animated
            )
            Text(
                modifier = Modifier.padding(
                    start = padding,
                    end = padding,
                    top = padding
                ),
                text = personProfile.id,
                fontSize = 20.sp
            )
            Text(
                modifier = Modifier.padding(horizontal = padding),
                text = personProfile.nickname,
                fontSize = 18.sp
            )
            Text(
                modifier = Modifier.padding(horizontal = padding),
                text = personProfile.signature,
                fontSize = 18.sp
            )
            Spacer(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(height = 30.dp)
            )
            SelectableItem(
                text = "个人资料",
                icon = Icons.Filled.Cabin,
                onClick = viewState.updateProfile
            )
            SelectableItem(
                text = "切换主题",
                icon = Icons.Filled.Sailing,
                onClick = viewState.switchTheme
            )
            SelectableItem(
                text = "切换账号",
                icon = Icons.Filled.ColorLens,
                onClick = viewState.logout
            )
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(weight = 1f, fill = true)
            )
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = padding),
                text = buildString {
                    append("公众号: 字节数组")
                    append("\n")
                    append("VersionCode: ")
                    append(BuildConfig.VERSION_CODE)
                    append("\n")
                    append("VersionName: ")
                    append(BuildConfig.VERSION_NAME)
                    append("\n")
                    append("BuildTime: ")
                    append(BuildConfig.BUILD_TIME)
                },
                textAlign = TextAlign.Center,
                fontSize = 15.sp
            )
        }
    }
}

@Composable
private fun AnimateBouncyImage(
    modifier: Modifier,
    faceUrl: String,
    animated: Boolean
) {
    val scale by animateFloatAsState(
        label = "",
        targetValue = if (animated) {
            1.8f
        } else {
            1f
        },
        animationSpec = tween(durationMillis = 1000)
    )
    val offset by animateIntOffsetAsState(
        label = "",
        targetValue = if (animated) {
            IntOffset(
                x = Random.nextInt(320, 480),
                y = Random.nextInt(540, 1200)
            )
        } else {
            IntOffset(x = 0, y = 0)
        }
    )
    BouncyImage(
        modifier = modifier
            .offset {
                offset
            }
            .scale(scale = scale),
        data = faceUrl
    )
}

@Composable
private fun SelectableItem(text: String, icon: ImageVector, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .height(height = 60.dp)
            .padding(start = 20.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            modifier = Modifier.size(size = 22.dp),
            imageVector = icon,
            tint = MaterialTheme.colorScheme.onSurface,
            contentDescription = null
        )
        Text(
            modifier = Modifier.padding(start = 10.dp),
            text = text,
            fontSize = 17.sp
        )
    }
}