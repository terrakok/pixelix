package com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.R
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.*
import com.daniebeler.pfpixelix.domain.model.Account
import com.daniebeler.pfpixelix.ui.composables.profile.other_profile.UnBlockAccountAlert
import com.daniebeler.pfpixelix.utils.Navigate

@Composable
fun CustomBlockedAccountRow(
    account: Account, navController: NavController, viewModel: BlockedAccountsViewModel
) {
    Row(
        Modifier
            .padding(horizontal = 12.dp, vertical = 8.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {

        Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable {
            Navigate.navigate("profile_screen/" + account.id, navController)
        }) {
            AsyncImage(
                model = account.avatar,
                error = painterResource(Res.drawable.default_avatar),
                contentDescription = "",
                modifier = Modifier
                    .height(32.dp)
                    .width(32.dp)
                    .clip(CircleShape)
            )
            Spacer(modifier = Modifier.width(10.dp))

            Text(text = account.username, fontWeight = FontWeight.Bold)

            Spacer(modifier = Modifier.weight(1f))

            Button(onClick = {
                viewModel.unblockAccountAlert = account.id
            }) {
                Text(text = stringResource(Res.string.unblock))
            }
        }
    }
    if (account.id == viewModel.unblockAccountAlert) {
        UnBlockAccountAlert(
            onDismissRequest = { viewModel.unblockAccountAlert = "" },
            onConfirmation = {
                viewModel.unblockAccountAlert = ""
                viewModel.unblockAccount(account.id) },
            account = account
        )
    }
}