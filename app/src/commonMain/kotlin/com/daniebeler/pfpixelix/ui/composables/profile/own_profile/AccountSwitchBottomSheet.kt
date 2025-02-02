package com.daniebeler.pfpixelix.ui.composables.profile.own_profile

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.daniebeler.pfpixelix.domain.model.loginDataToAccount
import com.daniebeler.pfpixelix.ui.composables.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.custom_account.CustomAccount
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.*

@Composable
fun AccountSwitchBottomSheet(
    closeBottomSheet: () -> Unit,
    ownProfileViewModel: OwnProfileViewModel?,
    viewModel: AccountSwitchViewModel = rememberViewModel(key = "account_switcher_viewmodel") { accountSwitchViewModel }
) {
    val app = LocalAppComponent.current
    val context = LocalKmpContext.current
    val showRemoveLoginDataAlert = remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        if (viewModel.currentlyLoggedIn.currentAccount != null) {
            Text(
                text = stringResource(Res.string.current_account),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
            CustomAccount(account = loginDataToAccount(viewModel.currentlyLoggedIn.currentAccount!!))
        }
        if (viewModel.otherAccounts.otherAccounts.isNotEmpty()) {
            Text(
                text = stringResource(Res.string.other_accounts),
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp,
                modifier = Modifier.padding(start = 12.dp)
            )
            viewModel.otherAccounts.otherAccounts.map { otherAccount ->
                Box(Modifier.clickable {
                    viewModel.switchAccount(otherAccount) {
                        ownProfileViewModel?.let {
                            ownProfileViewModel.updateAccountSwitch()
                        }
                        closeBottomSheet()
                    }
                }) {
                    CustomAccount(
                        account = loginDataToAccount(otherAccount),
                        logoutButton = true,
                        logout = {showRemoveLoginDataAlert.value = otherAccount.accountId})
                }
            }
        }
        Row(
            modifier = Modifier
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .fillMaxWidth()
                .clickable { app.contextNavigation.gotoLoginActivity(true) },
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(46.dp)
                    .height(46.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surfaceContainer),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "add account",
                    Modifier
                        .height(32.dp)
                        .width(32.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Text(
                text = stringResource(Res.string.add_pixelfed_account),
                lineHeight = 8.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }

    if (showRemoveLoginDataAlert.value.isNotBlank()) {
        AlertDialog(title = {
            Text(text = stringResource(Res.string.remove_account))
        }, text = {
            Text(text = stringResource(Res.string.are_you_sure_you_want_to_remove_this_account))
        }, onDismissRequest = {
            showRemoveLoginDataAlert.value = ""
        }, confirmButton = {
            TextButton(onClick = {
                CoroutineScope(Dispatchers.Default).launch {
                    viewModel.removeAccount(showRemoveLoginDataAlert.value)
                    showRemoveLoginDataAlert.value = ""
                }
            }) {
                Text(stringResource(Res.string.remove))
            }
        }, dismissButton = {
            TextButton(onClick = {
                showRemoveLoginDataAlert.value = ""
            }) {
                Text(stringResource(Res.string.cancel))
            }
        })
    }
}
