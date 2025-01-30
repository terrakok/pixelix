package com.daniebeler.pfpixelix.ui.composables.timelines.global_timeline

import androidx.compose.runtime.Composable
import com.daniebeler.pfpixelix.ui.composables.injectViewModel
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState

@Composable
fun GlobalTimelineComposable(
    navController: NavController,
    viewModel: GlobalTimelineViewModel = injectViewModel(key = "global-timeline-key") { globalTimelineViewModel }
) {
    InfinitePostsList(items = viewModel.globalTimelineState.globalTimeline,
        isLoading = viewModel.globalTimelineState.isLoading,
        isRefreshing = viewModel.globalTimelineState.refreshing,
        error = viewModel.globalTimelineState.error,
        endReached = false,
        navController = navController,
        getItemsPaginated = { viewModel.getItemsPaginated() },
        emptyMessage = EmptyState(heading = "No posts"),
        onRefresh = {
            viewModel.refresh()
        },
        itemGetsDeleted = { postId -> viewModel.postGetsDeleted(postId) })
}