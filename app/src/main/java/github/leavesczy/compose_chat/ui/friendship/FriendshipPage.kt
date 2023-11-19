package github.leavesczy.compose_chat.ui.friendship

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material3.Divider
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import github.leavesczy.compose_chat.base.models.GroupProfile
import github.leavesczy.compose_chat.base.models.PersonProfile
import github.leavesczy.compose_chat.ui.friendship.logic.FriendshipPageViewState
import github.leavesczy.compose_chat.ui.widgets.ComponentImage

/**
 * @Author: leavesCZY
 * @Desc:
 * @Github：https://github.com/leavesCZY
 */
@Composable
fun FriendshipPage(pageViewState: FriendshipPageViewState) {
    Box(modifier = Modifier.fillMaxSize()) {
        val joinedGroupList = pageViewState.joinedGroupList
        val friendList = pageViewState.friendList
        if (joinedGroupList.isEmpty() && friendList.isEmpty()) {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(fraction = 0.45f)
                    .wrapContentSize(align = Alignment.BottomCenter),
                text = "Empty",
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                fontSize = 70.sp
            )
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                state = pageViewState.listState,
                contentPadding = PaddingValues(bottom = 100.dp)
            ) {
                items(
                    items = joinedGroupList,
                    key = {
                        it.id
                    },
                    contentType = {
                        "Group"
                    },
                    itemContent = {
                        GroupItem(
                            groupProfile = it,
                            onClick = pageViewState.onClickGroupItem
                        )
                    }
                )
                items(
                    items = friendList,
                    key = {
                        it.id
                    },
                    contentType = {
                        "friend"
                    },
                    itemContent = {
                        FriendItem(
                            personProfile = it,
                            onClick = pageViewState.onClickFriendItem
                        )
                    }
                )
            }
        }
        FloatingActionButton(
            modifier = Modifier
                .align(alignment = Alignment.BottomEnd)
                .padding(bottom = 30.dp, end = 30.dp)
                .size(size = 50.dp),
            containerColor = MaterialTheme.colorScheme.primary,
            content = {
                Icon(
                    modifier = Modifier,
                    imageVector = Icons.Filled.Favorite,
                    tint = Color.White,
                    contentDescription = null,
                )
            },
            onClick = pageViewState.showFriendshipDialog
        )
    }
}

@Composable
private fun LazyItemScope.GroupItem(
    groupProfile: GroupProfile,
    onClick: (GroupProfile) -> Unit
) {
    Column(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .clickable(onClick = {
                onClick(groupProfile)
            })
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComponentImage(
                modifier = Modifier
                    .size(size = 50.dp)
                    .clip(shape = RoundedCornerShape(size = 6.dp)),
                model = groupProfile.faceUrl
            )
            Text(
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(start = 10.dp),
                text = groupProfile.name,
                fontSize = 18.sp,
                overflow = TextOverflow.Ellipsis,
                maxLines = 1
            )
        }
        Divider(
            modifier = Modifier
                .padding(start = 60.dp, top = 8.dp),
            thickness = 0.2.dp
        )
    }
}

@Composable
private fun LazyItemScope.FriendItem(
    personProfile: PersonProfile,
    onClick: (PersonProfile) -> Unit
) {
    Column(
        modifier = Modifier
            .animateItemPlacement()
            .fillMaxWidth()
            .clickable(onClick = {
                onClick(personProfile)
            })
            .padding(horizontal = 14.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            ComponentImage(
                modifier = Modifier
                    .size(size = 50.dp)
                    .clip(shape = RoundedCornerShape(size = 6.dp)),
                model = personProfile.faceUrl
            )
            Column(
                modifier = Modifier
                    .weight(weight = 1f)
                    .padding(start = 10.dp)
            ) {
                Text(
                    modifier = Modifier,
                    text = personProfile.showName,
                    fontSize = 18.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
                Text(
                    modifier = Modifier,
                    text = personProfile.signature,
                    fontSize = 15.sp,
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1
                )
            }
        }
        Divider(
            modifier = Modifier
                .padding(start = 60.dp, top = 8.dp),
            thickness = 0.2.dp
        )
    }
}