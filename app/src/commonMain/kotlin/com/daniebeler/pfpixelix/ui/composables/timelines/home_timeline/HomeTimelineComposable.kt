package com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.PhotoLibrary
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.utils.Navigate
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.explore_trending_profiles
import pixelix.app.generated.resources.follow_accounts_or_hashtags_to_fill_your_home_timeline
import pixelix.app.generated.resources.no_posts

@Composable
fun HomeTimelineComposable(
    navController: NavController,
    viewModel: HomeTimelineViewModel = rememberViewModel(key = "home-timeline-key") { homeTimelineViewModel }
) {
    Box(modifier = Modifier.fillMaxSize()) {
        InfinitePostsList(items = viewModel.homeTimelineState.homeTimeline,
            isLoading = viewModel.homeTimelineState.isLoading,
            isRefreshing = viewModel.homeTimelineState.refreshing,
            error = viewModel.homeTimelineState.error,
            endReached = false,
            navController = navController,
            emptyMessage = EmptyState(icon = Icons.Outlined.PhotoLibrary,
                heading = stringResource(Res.string.no_posts),
                message = stringResource(Res.string.follow_accounts_or_hashtags_to_fill_your_home_timeline),
                buttonText = stringResource(Res.string.explore_trending_profiles),
                onClick = {
                    Navigate.navigate("trending_screen/accounts", navController)
                }),
            getItemsPaginated = {
                viewModel.getItemsPaginated()
            },
            onRefresh = {
                viewModel.refresh()
            },
            itemGetsDeleted = { postId -> viewModel.postGetsDeleted(postId) })
    }
}