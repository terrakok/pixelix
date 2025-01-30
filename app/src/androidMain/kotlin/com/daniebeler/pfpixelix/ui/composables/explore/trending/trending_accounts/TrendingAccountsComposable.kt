package com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.composables.injectViewModel
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.R
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.*
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenLoadingComposable

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrendingAccountsComposable(
    navController: NavController,
    viewModel: TrendingAccountsViewModel = injectViewModel(key = "trending-accounts-key") { trendingAccountsViewModel }
) {
    PullToRefreshBox(
        isRefreshing = viewModel.trendingAccountsState.isRefreshing,
        onRefresh = { viewModel.getTrendingAccountsState(true) },
    ) {
        LazyColumn(modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(20.dp),
            content = {
                items(viewModel.trendingAccountsState.trendingAccounts, key = {
                    it.id
                }) {
                    TrendingAccountElement(
                        account = it, navController = navController
                    )
                }
            })
    }

    if (viewModel.trendingAccountsState.trendingAccounts.isEmpty()) {
        if (viewModel.trendingAccountsState.isLoading && !viewModel.trendingAccountsState.isRefreshing) {
            FullscreenLoadingComposable()
        }

        if (viewModel.trendingAccountsState.error.isNotEmpty()) {
            FullscreenErrorComposable(message = viewModel.trendingAccountsState.error)
        }

        if (!viewModel.trendingAccountsState.isLoading && viewModel.trendingAccountsState.error.isEmpty()) {
            FullscreenEmptyStateComposable(EmptyState(heading = stringResource(Res.string.no_trending_profiles)))
        }
    }
}