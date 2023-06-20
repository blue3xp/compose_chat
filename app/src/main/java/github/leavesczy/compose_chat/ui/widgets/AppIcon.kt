package github.leavesczy.compose_chat.ui.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import github.leavesczy.compose_chat.R

@Composable
private fun AppIcon(modifier: Modifier = Modifier) {
    Icon(
        modifier = modifier
            .size(size = 400.dp)
            .background(color = Color(0xFF0D82E0)),
        painter = painterResource(id = R.drawable.icon_app),
        tint = Color(0xFFFFFFFF),
        contentDescription = null
    )
}

@Preview
@Composable
private fun AppIconPreview() {
    AppIcon(modifier = Modifier)
}