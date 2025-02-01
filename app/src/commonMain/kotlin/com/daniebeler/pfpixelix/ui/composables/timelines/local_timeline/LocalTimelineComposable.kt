package com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline

import androidx.compose.runtime.Composable
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState

@Composable
fun LocalTimelineComposable(
    navController: NavController,
    viewModel: LocalTimelineViewModel = rememberViewModel(key = "local-timeline-key") { localTimelineViewModel }
) {
    InfinitePostsList(items = viewModel.localTimelineState.localTimeline,
        isLoading = viewModel.localTimelineState.isLoading,
        isRefreshing = viewModel.localTimelineState.refreshing,
        error = viewModel.localTimelineState.error,
        endReached = false,
        navController = navController,
        emptyMessage = EmptyState(heading = "No posts"),
        getItemsPaginated = {
            viewModel.getItemsPaginated()
        },
        onRefresh = {
            viewModel.refresh()
        },
        itemGetsDeleted = { postId -> viewModel.postGetsDeleted(postId) })
}