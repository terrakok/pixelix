package com.daniebeler.pfpixelix.ui.composables.settings.liked_posts

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsGrid
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.liked_posts
import pixelix.app.generated.resources.no_liked_posts

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LikedPostsComposable(
    navController: NavController,
    viewModel: LikedPostsViewModel = rememberViewModel(key = "likey-posts-key") { likedPostsViewModel }
) {

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        CenterAlignedTopAppBar(title = {
            Text(stringResource(Res.string.liked_posts), fontWeight = FontWeight.Bold)
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

            InfinitePostsGrid(items = viewModel.likedPostsState.likedPosts,
                isLoading = viewModel.likedPostsState.isLoading,
                isRefreshing = viewModel.likedPostsState.isRefreshing,
                error = viewModel.likedPostsState.error,
                emptyMessage = EmptyState(
                    icon = Icons.Outlined.FavoriteBorder,
                    heading = stringResource(Res.string.no_liked_posts)
                ),
                navController = navController,
                getItemsPaginated = {
                    viewModel.getItemsPaginated()
                },
                onRefresh = {
                    viewModel.getItemsFirstLoad(true)
                })
        }

    }
}