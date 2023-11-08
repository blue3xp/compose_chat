package github.leavesczy.compose_chat.ui.preview

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.SaveAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.lifecycleScope
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.theme.backgroundColorDark
import github.leavesczy.compose_chat.ui.widgets.ZoomableComponentImage
import github.leavesczy.compose_chat.utils.AlbumUtils
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class PreviewImageActivity : BaseActivity() {

    companion object {

        private const val keyImageUriList = "keyImageUriList"

        private const val keyInitialPage = "keyInitialPage"

        fun navTo(context: Context, imageUri: String) {
            navTo(context = context, imageUriList = listOf(element = imageUri), initialPage = 0)
        }

        fun navTo(context: Context, imageUriList: List<String>, initialPage: Int) {
            val intent = Intent(context, PreviewImageActivity::class.java)
            intent.putStringArrayListExtra(keyImageUriList, arrayListOf<String>().apply {
                addAll(imageUriList)
            })
            intent.putExtra(keyInitialPage, initialPage)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val imageUriList by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getStringArrayListExtra(keyImageUriList)?.filter { it.isNotBlank() } ?: emptyList()
    }

    private val initialPage by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getIntExtra(keyInitialPage, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreviewImagePage(
                imageUriList = imageUriList,
                initialPage = initialPage,
                insertImageToAlbum = ::insertImageToAlbum
            )
        }
    }

    @Composable
    override fun SetSystemBarUi() {
        val context = LocalContext.current
        LaunchedEffect(key1 = null) {
            if (context is Activity) {
                val window = context.window
                window.statusBarColor = android.graphics.Color.TRANSPARENT
                window.navigationBarColor = android.graphics.Color.TRANSPARENT
                WindowInsetsControllerCompat(window, window.decorView).apply {
                    hide(WindowInsetsCompat.Type.statusBars())
                    systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_DEFAULT
                    isAppearanceLightStatusBars = false
                    isAppearanceLightNavigationBars = false
                }
            }
        }
    }

    private fun insertImageToAlbum(imageUri: String) {
        lifecycleScope.launch {
            val result = AlbumUtils.insertImageToAlbum(
                context = applicationContext,
                imageUri = imageUri
            )
            if (result) {
                showToast(msg = "已保存到相册")
            } else {
                showToast(msg = "保存失败")
            }
        }
    }

}

@Composable
private fun PreviewImagePage(
    imageUriList: List<String>,
    initialPage: Int,
    insertImageToAlbum: (String) -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f
    ) {
        imageUriList.size
    }
    val requestPermissionLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            insertImageToAlbum(imageUriList[pagerState.currentPage])
        } else {
            ToastProvider.showToast(msg = "请先授予存储权限再保存图片")
        }
    }
    Scaffold(
        modifier = Modifier
            .background(color = backgroundColorDark)
            .fillMaxSize(),
        containerColor = backgroundColorDark,
        contentWindowInsets = WindowInsets(left = 0.dp, top = 0.dp, right = 0.dp, bottom = 0.dp),
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues = innerPadding)
        ) {
            HorizontalPager(
                modifier = Modifier
                    .fillMaxSize(),
                state = pagerState,
                pageSpacing = 0.dp,
                verticalAlignment = Alignment.CenterVertically
            ) { pageIndex ->
                PreviewPage(imageUrl = imageUriList[pageIndex])
            }
            IconButton(
                modifier = Modifier
                    .align(alignment = Alignment.BottomEnd)
                    .navigationBarsPadding()
                    .padding(all = 20.dp),
                content = {
                    Icon(
                        modifier = Modifier,
                        imageVector = Icons.Filled.SaveAlt,
                        tint = Color.White,
                        contentDescription = null
                    )
                },
                onClick = {
                    val imageUrl = imageUriList[pagerState.currentPage]
                    if (mustRequestWriteExternalStoragePermission(context = context)) {
                        requestPermissionLaunch.launch(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    } else {
                        insertImageToAlbum(imageUrl)
                    }
                }
            )
        }
    }
}

@Composable
private fun PreviewPage(imageUrl: String) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ZoomableComponentImage(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState()),
            model = imageUrl,
            contentScale = ContentScale.FillWidth
        )
    }
}

private fun mustRequestWriteExternalStoragePermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return false
    }
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED
}