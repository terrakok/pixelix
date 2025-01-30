package com.daniebeler.pfpixelix.ui.composables.profile.own_profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Block
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.DoNotDisturbOn
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Tag
import androidx.compose.material3.HorizontalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ImageBitmap
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.R
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.*
import com.daniebeler.pfpixelix.ui.composables.ButtonRowElement
import com.daniebeler.pfpixelix.utils.Navigate

@Composable
fun ModalBottomSheetContent(
    navController: NavController, instanceDomain: String, appIcon: ImageBitmap?, closeBottomSheet: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .verticalScroll(state = rememberScrollState())
            .padding(bottom = 12.dp)
    ) {

        ButtonRowElement(
            icon = Icons.Outlined.Settings,
            text = stringResource(Res.string.settings),
            onClick = {
                closeBottomSheet()
                Navigate.navigate("preferences_screen", navController)
            })

        HorizontalDivider(Modifier.padding(12.dp))

        ButtonRowElement(icon = Icons.Outlined.FavoriteBorder,
            text = stringResource(Res.string.liked_posts),
            onClick = {
                closeBottomSheet()
                Navigate.navigate("liked_posts_screen", navController)
            })

        ButtonRowElement(icon = Icons.Outlined.Bookmarks,
            text = stringResource(Res.string.bookmarked_posts),
            onClick = {
                closeBottomSheet()
                Navigate.navigate("bookmarked_posts_screen", navController)
            })

        ButtonRowElement(icon = Icons.Outlined.Tag,
            text = stringResource(Res.string.followed_hashtags),
            onClick = {
                closeBottomSheet()
                Navigate.navigate("followed_hashtags_screen", navController)
            })

        ButtonRowElement(icon = Icons.Outlined.DoNotDisturbOn,
            text = stringResource(Res.string.muted_accounts),
            onClick = {
                closeBottomSheet()
                Navigate.navigate("muted_accounts_screen", navController)
            })

        ButtonRowElement(icon = Icons.Outlined.Block,
            text = stringResource(Res.string.blocked_accounts),
            onClick = {
                closeBottomSheet()
                Navigate.navigate("blocked_accounts_screen", navController)
            })

        HorizontalDivider(Modifier.padding(12.dp))

        ButtonRowElement(icon = R.drawable.pixelfed_logo,
            text = stringResource(Res.string.about_x, instanceDomain),
            onClick = {
                closeBottomSheet()
                Navigate.navigate("about_instance_screen", navController)
            })

        if (appIcon == null) {
            ButtonRowElement(icon = R.drawable.pixelix_logo,
                text = stringResource(Res.string.about_pixelix),
                onClick = {
                    closeBottomSheet()
                    Navigate.navigate("about_pixelix_screen", navController)
                })
        } else {
            ButtonRowElement(icon = appIcon,
                text = stringResource(Res.string.about_pixelix),
                onClick = {
                    closeBottomSheet()
                    Navigate.navigate("about_pixelix_screen", navController)
                })
        }

    }
}