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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import androidx.lifecycle.lifecycleScope
import coil.compose.AsyncImage
import github.leavesczy.compose_chat.provider.ToastProvider
import github.leavesczy.compose_chat.ui.base.BaseActivity
import github.leavesczy.compose_chat.ui.base.setSystemBarUi
import github.leavesczy.compose_chat.ui.theme.BackgroundColorDark
import github.leavesczy.compose_chat.ui.theme.WindowInsetsEmpty
import github.leavesczy.compose_chat.utils.AlbumUtils
import kotlinx.coroutines.launch

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
class PreviewImageActivity : BaseActivity() {

    companion object {

        private const val keyImageUrlList = "keyImageUrlList"

        private const val keyInitialPage = "keyInitialPage"

        fun navTo(context: Context, imageUrl: String) {
            navTo(context = context, imageUrlList = listOf(element = imageUrl), initialPage = 0)
        }

        fun navTo(context: Context, imageUrlList: List<String>, initialPage: Int) {
            val intent = Intent(context, PreviewImageActivity::class.java)
            intent.putStringArrayListExtra(keyImageUrlList, arrayListOf<String>().apply {
                addAll(imageUrlList)
            })
            intent.putExtra(keyInitialPage, initialPage)
            if (context !is Activity) {
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            }
            context.startActivity(intent)
        }

    }

    private val imageUrlList by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getStringArrayListExtra(keyImageUrlList)?.filter { it.isNotBlank() } ?: emptyList()
    }

    private val initialPage by lazy(mode = LazyThreadSafetyMode.NONE) {
        intent.getIntExtra(keyInitialPage, 0)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            PreviewImagePage(
                imageUrlList = imageUrlList,
                initialPage = initialPage,
                insertImageToAlbum = ::insertImageToAlbum
            )
        }
    }

    override fun setSystemBarUi() {
        setSystemBarUi(
            statusBarColor = android.graphics.Color.TRANSPARENT,
            navigationBarColor = android.graphics.Color.TRANSPARENT,
            statusBarDarkIcons = false,
            navigationBarDarkIcons = false
        )
    }

    private fun insertImageToAlbum(imageUrl: String) {
        lifecycleScope.launch {
            val result =
                AlbumUtils.insertImageToAlbum(context = applicationContext, imageUrl = imageUrl)
            if (result) {
                showToast(msg = "图片已保存到相册")
            } else {
                showToast(msg = "图片保存失败")
            }
        }
    }

}

@Composable
private fun PreviewImagePage(
    imageUrlList: List<String>,
    initialPage: Int,
    insertImageToAlbum: (String) -> Unit
) {
    val context = LocalContext.current
    val pagerState = rememberPagerState(
        initialPage = initialPage,
        initialPageOffsetFraction = 0f
    ) {
        imageUrlList.size
    }
    val requestPermissionLaunch = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) {
        if (it) {
            insertImageToAlbum(imageUrlList[pagerState.currentPage])
        } else {
            ToastProvider.showToast(msg = "请先授予存储权限再保存图片")
        }
    }
    Scaffold(
        modifier = Modifier
            .background(color = BackgroundColorDark)
            .fillMaxSize(),
        containerColor = BackgroundColorDark,
        contentWindowInsets = WindowInsetsEmpty,
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
                PreviewPage(imageUrl = imageUrlList[pageIndex])
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
                    val imageUrl = imageUrlList[pagerState.currentPage]
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
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AsyncImage(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(state = rememberScrollState()),
            model = imageUrl,
            contentScale = ContentScale.FillWidth,
            contentDescription = null
        )
    }
}

private fun mustRequestWriteExternalStoragePermission(context: Context): Boolean {
    if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M || Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
        return false
    }
    return ActivityCompat.checkSelfPermission(
        context,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    ) != PackageManager.PERMISSION_GRANTED
}