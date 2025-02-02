package com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenLoadingComposable
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.blocked_accounts
import pixelix.app.generated.resources.no_blocked_accounts

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun BlockedAccountsComposable(
    navController: NavController,
    viewModel: BlockedAccountsViewModel = rememberViewModel(key = "blocked-accounts-key") { blockedAccountsViewModel }
) {
    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        CenterAlignedTopAppBar(title = {
            Text(
                text = stringResource(Res.string.blocked_accounts), fontWeight = FontWeight.Bold
            )
        }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos, contentDescription = ""
                )
            }
        })

    }) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            PullToRefreshBox(
                isRefreshing = viewModel.blockedAccountsState.isRefreshing,
                onRefresh = { viewModel.getBlockedAccounts(true) },
            ) {
                LazyColumn(
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(viewModel.blockedAccountsState.blockedAccounts, key = {
                        it.id
                    }) {
                        Row {
                            CustomBlockedAccountRow(
                                account = it, navController = navController, viewModel
                            )
                        }
                    }
                }
            }

            if (viewModel.blockedAccountsState.blockedAccounts.isEmpty()) {
                if (viewModel.blockedAccountsState.isLoading && !viewModel.blockedAccountsState.isRefreshing) {
                    FullscreenLoadingComposable()
                }

                if (viewModel.blockedAccountsState.error.isNotEmpty()) {
                    FullscreenErrorComposable(message = viewModel.blockedAccountsState.error)
                }

                if (!viewModel.blockedAccountsState.isLoading && viewModel.blockedAccountsState.error.isEmpty()) {
                    FullscreenEmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_blocked_accounts)))
                }
            }
        }
    }
}