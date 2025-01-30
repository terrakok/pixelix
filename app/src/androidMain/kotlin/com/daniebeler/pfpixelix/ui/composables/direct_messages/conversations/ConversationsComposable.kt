package com.daniebeler.pfpixelix.ui.composables.direct_messages.conversations

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.QuestionMark
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import org.jetbrains.compose.resources.stringResource
import androidx.compose.ui.text.TextRange
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import com.daniebeler.pfpixelix.ui.composables.injectViewModel
import androidx.navigation.NavController
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.*
import com.daniebeler.pfpixelix.ui.composables.InfiniteListHandler
import com.daniebeler.pfpixelix.ui.composables.SheetItem
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.FullscreenEmptyStateComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.utils.Navigate

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterialApi::class)
@Composable
fun ConversationsComposable(
    navController: NavController,
    viewModel: ConversationsViewModel = injectViewModel(key = "conversations-viewmodel-key") { conversationsViewModel }
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showBottomSheet by remember { mutableStateOf(false) }
    val showNewChatDialog = remember { mutableStateOf(false) }

    val lazyListState = rememberLazyListState()

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), floatingActionButton = {
        FloatingActionButton(onClick = {
            showNewChatDialog.value = true
        }) {
            Icon(Icons.Default.Add, contentDescription = "Add")
        }

    }, topBar = {
        CenterAlignedTopAppBar(title = {
            Text(stringResource(Res.string.conversations), fontWeight = FontWeight.Bold)

        }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos, contentDescription = ""
                )
            }
        }, actions = {
            IconButton(onClick = { showBottomSheet = true }) {
                Icon(
                    imageVector = Icons.Outlined.QuestionMark,
                    tint = MaterialTheme.colorScheme.error,
                    contentDescription = null
                )
            }
        })
    }) { paddingValues ->
        PullToRefreshBox(
            isRefreshing = viewModel.conversationsState.isRefreshing,
            onRefresh = { viewModel.refresh() },
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            LazyColumn(state = lazyListState, modifier = Modifier.fillMaxSize(), content = {
                if (viewModel.conversationsState.conversations.isNotEmpty()) {
                    items(viewModel.conversationsState.conversations, key = {
                        it.id
                    }) {
                        ConversationElementComposable(
                            conversation = it, navController = navController
                        )
                    }

                    if (viewModel.conversationsState.isLoading && !viewModel.conversationsState.isRefreshing) {
                        item {
                            CircularProgressIndicator(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(80.dp)
                                    .wrapContentSize(Alignment.Center)
                            )
                        }
                    }

                    if (viewModel.conversationsState.endReached && viewModel.conversationsState.conversations.size > 10) {
                        item {
                            EndOfListComposable()
                        }
                    }
                }
            })

            if (!viewModel.conversationsState.isLoading && viewModel.conversationsState.error.isEmpty() && viewModel.conversationsState.conversations.isEmpty()) {
                FullscreenEmptyStateComposable(
                    EmptyState(
                        icon = Icons.Outlined.Email, heading = stringResource(
                            Res.string.you_don_t_have_any_notifications
                        )
                    )
                )
            }

            if (!viewModel.conversationsState.isRefreshing && viewModel.conversationsState.conversations.isEmpty()) {
                LoadingComposable(isLoading = viewModel.conversationsState.isLoading)
            }
            ErrorComposable(message = viewModel.conversationsState.error)
        }

        InfiniteListHandler(lazyListState = lazyListState) {
            //viewModel.getNotificationsPaginated()
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp)
                ) {
                    Column {
                        Spacer(modifier = Modifier.height(18.dp))

                        SheetItem(
                            header = stringResource(Res.string.warning),
                            description = stringResource(Res.string.direct_messages_encryption_description)
                        )

                        Spacer(modifier = Modifier.height(18.dp))
                    }
                }
            }
        }
        if (showNewChatDialog.value) {
            CreateNewConversation(
                close = {
                    showNewChatDialog.value = false
                    viewModel.newConversationUsername = TextFieldValue()
                    viewModel.newConversationSelectedAccount = null
                    viewModel.newConversationState = NewConversationState()
                }, viewModel, navController
            )
        }
    }
}

@Composable
private fun CreateNewConversation(
    close: () -> Unit, viewModel: ConversationsViewModel, navController: NavController
) {

    AlertDialog(title = {
        Text(text = stringResource(Res.string.new_direct_message))
    }, text = {
        Column {
            OutlinedTextField(
                value = viewModel.newConversationUsername,
                onValueChange = {
                    viewModel.changeNewConversationUsername(it)
                },
                label = { Text(stringResource(Res.string.select_recipient)) },
                shape = RoundedCornerShape(12.dp),
            )
            if (viewModel.newConversationState.suggestions.isNotEmpty()) {
                Box(
                    modifier = Modifier
                        .padding(top = 4.dp)
                        .clip(shape = RoundedCornerShape(12.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp)
                    ) {
                        viewModel.newConversationState.suggestions.map {
                            Box(modifier = Modifier
                                .fillMaxWidth()
                                .padding(8.dp)
                                .clickable {
                                    viewModel.newConversationUsername = TextFieldValue(
                                        it.acct, selection = TextRange(it.acct.length)
                                    )
                                    viewModel.newConversationSelectedAccount = it
                                    viewModel.newConversationState = NewConversationState()
                                }) {
                                Text(
                                    text = "@${it.acct}",
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }
                    }
                }
            }
        }
    }, onDismissRequest = {
        close()
    }, confirmButton = {
        TextButton(enabled = viewModel.newConversationSelectedAccount != null, onClick = {
            if (viewModel.newConversationSelectedAccount != null) {
                Navigate.navigate(
                    "chat/" + viewModel.newConversationSelectedAccount!!.id, navController
                )
                close()
            }
        }) {
            Text(stringResource(Res.string.confirm))
        }
    }, dismissButton = {
        TextButton(onClick = {
            close()
        }) {
            Text(stringResource(Res.string.cancel))
        }
    })

}