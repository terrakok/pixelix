package com.daniebeler.pfpixelix.ui.composables.notifications

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Widgets
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.InfiniteListHandler
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.ui.composables.states.*
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsComposable(
    navController: NavController,
    viewModel: NotificationsViewModel = rememberViewModel(key = "notifications-viewmodel-key") { notificationsViewModel }
) {

    val lazyListState = rememberLazyListState()
    val scrollState = rememberScrollState()
    val context: KmpContext = LocalKmpContext.current

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        CenterAlignedTopAppBar(title = {
            Text(stringResource(Res.string.notifications), fontWeight = FontWeight.Bold)
        }, actions = {
            IconButton(onClick = {
                pinWidget(context)
            }) {
                Icon(imageVector = Icons.Outlined.Widgets, contentDescription = "add widget")
            }
        })
    }) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            Column {
                Row(modifier = Modifier.horizontalScroll(scrollState)) {
                    Spacer(modifier = Modifier.width(12.dp))
                    if (viewModel.filter == NotificationsFilterEnum.All) {
                        ActiveFilterButton(text = stringResource(Res.string.all))
                    } else {
                        InactiveFilterButton(text = stringResource(Res.string.all), onClick = {
                            viewModel.changeFilter(NotificationsFilterEnum.All)
                        })
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (viewModel.filter == NotificationsFilterEnum.Followers) {
                        ActiveFilterButton(text = stringResource(Res.string.followers))
                    } else {
                        InactiveFilterButton(
                            text = stringResource(Res.string.followers),
                            onClick = {
                                viewModel.changeFilter(NotificationsFilterEnum.Followers)
                            })
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (viewModel.filter == NotificationsFilterEnum.Likes) {
                        ActiveFilterButton(text = stringResource(Res.string.likes_))
                    } else {
                        InactiveFilterButton(text = stringResource(Res.string.likes_), onClick = {
                            viewModel.changeFilter(NotificationsFilterEnum.Likes)
                        })
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (viewModel.filter == NotificationsFilterEnum.Reposts) {
                        ActiveFilterButton(text = stringResource(Res.string.reposts))
                    } else {
                        InactiveFilterButton(text = stringResource(Res.string.reposts), onClick = {
                            viewModel.changeFilter(NotificationsFilterEnum.Reposts)
                        })
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    if (viewModel.filter == NotificationsFilterEnum.Mentions) {
                        ActiveFilterButton(text = stringResource(Res.string.mentions))
                    } else {
                        InactiveFilterButton(text = stringResource(Res.string.mentions), onClick = {
                            viewModel.changeFilter(NotificationsFilterEnum.Mentions)
                        })
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                PullToRefreshBox(
                    isRefreshing = viewModel.notificationsState.isRefreshing,
                    onRefresh = { viewModel.refresh() },
                ) {
                    LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize(), content = {
                        if (viewModel.notificationsState.notifications.isNotEmpty()) {
                            items(viewModel.notificationsState.notifications, key = {
                                it.id
                            }) {
                                if (viewModel.filter == NotificationsFilterEnum.All) {
                                    CustomNotification(
                                        notification = it, navController = navController
                                    )
                                } else if (viewModel.filter == NotificationsFilterEnum.Likes && it.type == "favourite") {
                                    CustomNotification(
                                        notification = it, navController = navController
                                    )
                                } else if (viewModel.filter == NotificationsFilterEnum.Followers && it.type == "follow") {
                                    CustomNotification(
                                        notification = it, navController = navController
                                    )
                                } else if (viewModel.filter == NotificationsFilterEnum.Reposts && it.type == "reblog") {
                                    CustomNotification(
                                        notification = it, navController = navController
                                    )
                                } else if (viewModel.filter == NotificationsFilterEnum.Mentions && it.type == "mention") {
                                    CustomNotification(
                                        notification = it, navController = navController
                                    )
                                }
                            }

                            if (viewModel.notificationsState.isLoading && !viewModel.notificationsState.isRefreshing) {
                                item {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .wrapContentSize(Alignment.Center)
                                    )
                                }
                            }

                            if (viewModel.notificationsState.endReached && viewModel.notificationsState.notifications.size > 10) {
                                item {
                                    EndOfListComposable()
                                }
                            }
                        }
                    })
                }
            }


            if (!viewModel.notificationsState.isLoading && viewModel.notificationsState.error.isEmpty() && viewModel.notificationsState.notifications.isEmpty()) {
                FullscreenEmptyStateComposable(
                    EmptyState(
                        icon = Icons.Outlined.Email, heading = stringResource(
                            Res.string.you_don_t_have_any_notifications
                        )
                    )
                )
            }

            if (!viewModel.notificationsState.isRefreshing && viewModel.notificationsState.notifications.isEmpty()) {
                LoadingComposable(isLoading = viewModel.notificationsState.isLoading)
            }
            ErrorComposable(message = viewModel.notificationsState.error)
        }

        InfiniteListHandler(lazyListState = lazyListState) {
            viewModel.getNotificationsPaginated()
        }
    }
}

private fun pinWidget(context: KmpContext) {
    //todo
//    val appWidgetManager = AppWidgetManager.getInstance(context)
//    val myProvider = ComponentName(context, NotificationWidgetReceiver::class.java)
//
//    if (appWidgetManager.isRequestPinAppWidgetSupported) {
//        appWidgetManager.requestPinAppWidget(myProvider, null, null)
//    }
}

@Composable
private fun ActiveFilterButton(text: String) {
    Button(onClick = { }, shape = RoundedCornerShape(12.dp)) {
        Text(text = text)
    }
}

@Composable
private fun InactiveFilterButton(text: String, onClick: () -> Unit) {
    Button(
        onClick = { onClick() },
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainer,
            contentColor = MaterialTheme.colorScheme.onSurface
        )
    ) {
        Text(text = text)
    }
}