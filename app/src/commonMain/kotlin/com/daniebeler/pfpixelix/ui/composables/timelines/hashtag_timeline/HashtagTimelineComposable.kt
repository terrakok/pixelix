package com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.FollowButton
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsList
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.utils.Navigate
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HashtagTimelineComposable(
    navController: NavController,
    hashtag: String,
    viewModel: HashtagTimelineViewModel = rememberViewModel(key = "hashtag-timeline$hashtag") { hashtagTimelineViewModel }
) {

    LaunchedEffect(hashtag) {
        viewModel.getItemsFirstLoad(hashtag)
        viewModel.getHashtagInfo(hashtag)
        viewModel.getRelatedHashtags(hashtag)
    }

    val scrollState = rememberScrollState()

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(
                scrollBehavior = scrollBehavior,
                title = {
                    Column (horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            "#$hashtag",
                            fontWeight = FontWeight.Bold,
                            overflow = TextOverflow.Ellipsis,
                            maxLines = 1
                        )
                        if (viewModel.hashtagState.hashtag != null) {
                            Text(
                                //todo
//                                text = String.format(Locale.GERMANY, "%,d", viewModel.hashtagState.hashtag!!.count) + " " + stringResource(Res.string.posts),
                                text = "${viewModel.hashtagState.hashtag!!.count} ${stringResource(Res.string.posts)}",
                                fontSize = 14.sp,
                                lineHeight = 12.sp,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                    }

                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    FollowButton(firstLoaded = viewModel.hashtagState.hashtag != null,
                        isLoading = viewModel.hashtagState.isLoading,
                        isFollowing = viewModel.hashtagState.hashtag?.following ?: false,
                        onFollowClick = { viewModel.followHashtag(viewModel.hashtagState.hashtag!!.name) },
                        onUnFollowClick = { viewModel.unfollowHashtag(viewModel.hashtagState.hashtag!!.name) })
                })

        }) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            InfinitePostsList(items = viewModel.postsState.hashtagTimeline,
                isLoading = viewModel.postsState.isLoading,
                isRefreshing = viewModel.postsState.isRefreshing,
                error = viewModel.postsState.error,
                endReached = viewModel.postsState.endReached,
                navController = navController,
                emptyMessage = EmptyState(heading = "No posts"),
                getItemsPaginated = {
                    viewModel.getItemsPaginated(hashtag)
                },
                onRefresh = {
                    viewModel.refresh()
                },
                itemGetsDeleted = { postId -> viewModel.postGetsDeleted(postId) },
                before = {
                    if (viewModel.relatedHashtags.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        LazyRow {
                            items(viewModel.relatedHashtags) {
                                Spacer(modifier = Modifier.width(12.dp))
                                Button(
                                    onClick = {
                                        Navigate.navigate(
                                            "hashtag_timeline_screen/${it.name}", navController
                                        )
                                    }, colors = ButtonDefaults.buttonColors(
                                        containerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        contentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Text(text = it.name)
                                }
                            }

                            item {
                                Spacer(modifier = Modifier.width(12.dp))
                            }
                        }

                    }


                })
        }
    }
}