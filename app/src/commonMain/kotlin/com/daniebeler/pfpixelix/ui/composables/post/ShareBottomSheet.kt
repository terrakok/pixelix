package com.daniebeler.pfpixelix.ui.composables.post

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.common.Constants
import com.daniebeler.pfpixelix.domain.model.MediaAttachment
import com.daniebeler.pfpixelix.domain.model.Post
import com.daniebeler.pfpixelix.ui.composables.ButtonRowElement
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.Navigate
import com.daniebeler.pfpixelix.utils.Share
import org.jetbrains.compose.resources.getString
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.*

@Composable
fun ShareBottomSheet(
    context: KmpContext,
    url: String,
    minePost: Boolean,
    viewModel: PostViewModel,
    post: Post,
    currentMediaAttachmentNumber: Int,
    navController: NavController
) {

    var humanReadableVisibility by remember {
        mutableStateOf("")
    }

    val mediaAttachment: MediaAttachment? = viewModel.post?.mediaAttachments?.let { attachments ->
        if (attachments.isNotEmpty() && currentMediaAttachmentNumber in attachments.indices) {
            attachments[currentMediaAttachmentNumber]
        } else {
            null
        }
    }

    LaunchedEffect(Unit) {
        humanReadableVisibility = if (post.visibility == Constants.AUDIENCE_PUBLIC) {
            getString(Res.string.audience_public)
        } else if (post.visibility == Constants.AUDIENCE_UNLISTED) {
            getString(Res.string.unlisted)
        } else if (post.visibility == Constants.AUDIENCE_FOLLOWERS_ONLY) {
            getString(Res.string.followers_only)
        } else {
            post.visibility
        }
    }


    Column(
        modifier = Modifier.padding(bottom = 32.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically, modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Outlined.Visibility,
                contentDescription = "",
                Modifier.padding(start = 18.dp, top = 12.dp, bottom = 12.dp)
            )

            Spacer(modifier = Modifier.width(12.dp))

            Text(text = stringResource(Res.string.visibility_x, humanReadableVisibility))
        }
        if (mediaAttachment?.license != null) {
            ButtonRowElement(icon = Icons.Outlined.Description, text = stringResource(
                Res.string.license, mediaAttachment.license?.title.orEmpty()
            ), onClick = {
                viewModel.openUrl(context, mediaAttachment.license?.url.orEmpty())
            })
        }

        HorizontalDivider(Modifier.padding(12.dp))

        ButtonRowElement(icon = Icons.Outlined.OpenInBrowser, text = stringResource(
            Res.string.open_in_browser
        ), onClick = {
            viewModel.openUrl(context, url)
        })

        ButtonRowElement(icon = Icons.Outlined.Share,
            text = stringResource(Res.string.share_this_post),
            onClick = {
                Share.shareText(context, url)
            })

        //todo
//        if (mediaAttachment != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q && mediaAttachment.type == "image") {
        if (mediaAttachment != null && mediaAttachment.type == "image") {
            ButtonRowElement(icon = Icons.Outlined.Download,
                text = stringResource(Res.string.download_image),
                onClick = {

                    viewModel.saveImage(
                        post.account.username,
                        viewModel.post!!.mediaAttachments[currentMediaAttachmentNumber].url!!,
                        context
                    )
                })
        }

        if (minePost) {
            HorizontalDivider(Modifier.padding(12.dp))

            ButtonRowElement(
                icon = Icons.Outlined.Edit,
                text = stringResource(Res.string.edit_post),
                onClick = {
                    Navigate.navigate("edit_post_screen/${post.id}", navController = navController)
                }
            )
            ButtonRowElement(
                icon = Icons.Outlined.Delete,
                text = stringResource(Res.string.delete_this_post),
                onClick = {
                    viewModel.deleteDialog = post.id
                },
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}
