package com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import com.daniebeler.pfpixelix.ui.composables.injectViewModel
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.R
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsGrid
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookmarkedPostsComposable(
    navController: NavController,
    viewModel: BookmarkedPostsViewModel = injectViewModel(key = "bookmarksviewmodel") { bookmarkedPostsViewModel }
) {

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        CenterAlignedTopAppBar(title = {
            Text(stringResource(id = R.string.bookmarked_posts), fontWeight = FontWeight.Bold)
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
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InfinitePostsGrid(items = viewModel.bookmarkedPostsState.bookmarkedPosts,
                isLoading = viewModel.bookmarkedPostsState.isLoading,
                isRefreshing = viewModel.bookmarkedPostsState.isRefreshing,
                error = viewModel.bookmarkedPostsState.error,
                endReached = false,
                emptyMessage = EmptyState(heading = stringResource(R.string.no_bookmarked_posts)),
                navController = navController,
                getItemsPaginated = { /*TODO*/ },
                onRefresh = { viewModel.getBookmarkedPosts(true) })
        }
    }
}