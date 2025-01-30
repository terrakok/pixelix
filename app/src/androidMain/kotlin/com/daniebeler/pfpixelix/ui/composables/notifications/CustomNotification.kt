package com.daniebeler.pfpixelix.ui.composables.notifications

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniebeler.pfpixelix.ui.composables.injectViewModel
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.R
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.*
import com.daniebeler.pfpixelix.domain.model.Notification
import com.daniebeler.pfpixelix.utils.Navigate

@Composable
fun CustomNotification(
    notification: Notification,
    navController: NavController,
    viewModel: CustomNotificationViewModel = injectViewModel(key = "custom-notification-viewmodel-key${notification.id}") { customNotificationViewModel }
) {
    var showImage = false
    var text = ""
    when (notification.type) {
        "follow" -> {
            text = " " + stringResource(Res.string.followed_you)
        }

        "mention" -> {
            text = " " + stringResource(Res.string.mentioned_you_in_a_post)
            showImage = true
        }

        "direct" -> {
            text = " " + stringResource(Res.string.sent_a_dm)
        }

        "favourite" -> {
            text = " " + stringResource(Res.string.liked_your_post)
            showImage = true
        }

        "reblog" -> {
            text = " " + stringResource(Res.string.reblogged_your_post)
            showImage = true
        }
    }

    LaunchedEffect(notification) {
        if (notification.type == "mention" && notification.post?.inReplyToId != null && !notification.post?.inReplyToId.isNullOrBlank()) {
            viewModel.loadAncestor(notification.post?.inReplyToId.orEmpty())
        }
    }

    Row(
        Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth().clickable {
            if (notification.post != null && notification.post?.mediaAttachments.isNullOrEmpty()) {
                Navigate.navigate("mention/" + notification.post?.id, navController)
            }
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        AsyncImage(model = notification.account.avatar,
            error = painterResource(Res.drawable.default_avatar),
            contentDescription = "",
            modifier = Modifier
                .height(46.dp)
                .width(46.dp)
                .clip(CircleShape)
                .clickable {
                    Navigate.navigate("profile_screen/" + notification.account.id, navController)
                })
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = notification.account.username, fontWeight = FontWeight.Bold, modifier = Modifier.clickable {
                    Navigate.navigate("profile_screen/" + notification.account.id, navController)
                })

                Text(text = text, overflow = TextOverflow.Ellipsis)
            }

            Text(
                text = notification.timeAgo,
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.primary
            )
        }

        val doesMediaAttachmentExsist = (notification.post?.mediaAttachments?.size
            ?: 0) > 0
        if (showImage && (doesMediaAttachmentExsist || (viewModel.ancestor != null && viewModel.ancestor!!.mediaAttachments.isNotEmpty()))
        ) {
            val previewUrl = if (doesMediaAttachmentExsist) {
                notification.post?.mediaAttachments?.get(0)?.previewUrl
            } else {
                viewModel.ancestor?.mediaAttachments?.get(0)?.previewUrl
            }
            Spacer(modifier = Modifier.weight(1f))
            AsyncImage(model = previewUrl,
                contentDescription = "",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .height(36.dp)
                    .aspectRatio(1f)
                    .clip(RoundedCornerShape(4.dp))
                    .clickable {
                        Navigate.navigate(
                            "single_post_screen/" + if (doesMediaAttachmentExsist) {notification.post!!.id} else {viewModel.ancestor!!.id + "?openReplies=true"}, navController
                        )
                    })
        }
    }
}