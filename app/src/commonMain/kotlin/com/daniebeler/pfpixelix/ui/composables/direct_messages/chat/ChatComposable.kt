package com.daniebeler.pfpixelix.ui.composables.direct_messages.chat

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.ui.composables.InfiniteListHandler
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EndOfListComposable
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import com.daniebeler.pfpixelix.utils.Navigate
import com.daniebeler.pfpixelix.utils.imeAwareInsets
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.beginning_of_chat_note
import pixelix.app.generated.resources.default_avatar

@OptIn(ExperimentalMaterial3Api::class)

@Composable
fun ChatComposable(
    navController: NavController,
    accountId: String,
    viewModel: ChatViewModel = rememberViewModel(key = "chat$accountId") { chatViewModel }
) {
    val lazyListState = rememberLazyListState()
    val context = LocalKmpContext.current
    LaunchedEffect(Unit) {
        viewModel.getChat(accountId)
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        TopAppBar(title = {
            if (viewModel.chatState.chat != null) {
                Row(
                    modifier = Modifier.clickable {
                        Navigate.navigate("profile_screen/$accountId", navController)
                    }, verticalAlignment = Alignment.CenterVertically
                ) {
                    AsyncImage(
                        model = viewModel.chatState.chat!!.avatar,
                        error = painterResource(Res.drawable.default_avatar),
                        contentDescription = "",
                        modifier = Modifier
                            .height(46.dp)
                            .width(46.dp)
                            .clip(CircleShape)
                    )
                    Spacer(modifier = Modifier.width(10.dp))

                    Column {

                        Text(text = viewModel.chatState.chat!!.username ?: "")
                        Text(
                            text = viewModel.chatState.chat!!.url.substringAfter("https://")
                                .substringBefore("/"),
                            fontSize = 12.sp,
                            lineHeight = 6.sp,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }
            }

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

        PullToRefreshBox (
            isRefreshing = viewModel.chatState.isRefreshing,
            onRefresh = { viewModel.getChat(accountId, true) },
            modifier = Modifier
                .imeAwareInsets(90.dp)
                .padding(paddingValues)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(12.dp)
            ) {
                LazyColumn(state = lazyListState,
                    modifier = Modifier.weight(1f),
                    reverseLayout = true,
                    content = {
                        if (viewModel.chatState.chat != null && viewModel.chatState.chat?.messages!!.isNotEmpty()) {

                            items(viewModel.chatState.chat!!.messages, key = {
                                it.id
                            }) {
                                ConversationElementComposable(
                                    message = it, { viewModel.deleteMessage(it.reportId) }, navController = navController
                                )
                            }

                            if (viewModel.chatState.isLoading) {
                                item {
                                    CircularProgressIndicator(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(80.dp)
                                            .wrapContentSize(Alignment.Center)
                                    )
                                }
                            }

                            if (viewModel.chatState.endReached) {
                                item {
                                    EndOfListComposable()
                                }
                            }
                        }

                        if (viewModel.chatState.chat != null && viewModel.chatState.chat?.messages?.isEmpty() == true) {
                            item {
                                Spacer(modifier = Modifier.height(56.dp))
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(
                                            RoundedCornerShape(8.dp)
                                        )
                                        .background(MaterialTheme.colorScheme.primaryContainer)
                                        .padding(8.dp)
                                ) {
                                    Text(
                                        text = stringResource(Res.string.beginning_of_chat_note),
                                        textAlign = TextAlign.Center,
                                        modifier = Modifier.fillMaxWidth()
                                    )
                                }
                            }
                        }
                    })


                Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Bottom) {
                    OutlinedTextField(value = viewModel.newMessage,
                        onValueChange = { viewModel.newMessage = it },
                        label = { Text("Message") },
                        singleLine = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedBorderColor = MaterialTheme.colorScheme.background
                        ),
                        shape = RoundedCornerShape(12.dp),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Default),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(Modifier.width(12.dp))
                    if (viewModel.newMessageState.isLoading) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier
                                .height(56.dp)
                                .width(56.dp)
                                .padding(0.dp, 0.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .background(MaterialTheme.colorScheme.primary)
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(24.dp),
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    } else {
                        Button(
                            onClick = {
                                viewModel.sendMessage(accountId)
                            },
                            Modifier
                                .height(56.dp)
                                .width(56.dp)
                                .padding(0.dp, 0.dp),
                            shape = RoundedCornerShape(12.dp),
                            contentPadding = PaddingValues(12.dp)
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.Send,
                                contentDescription = "send",
                                Modifier
                                    .fillMaxSize()
                                    .fillMaxWidth()
                            )
                        }
                    }
                }

                ErrorComposable(message = viewModel.chatState.error)
            }
        }
        InfiniteListHandler(lazyListState = lazyListState) {
            viewModel.getChatPaginated(accountId)
        }
    }
}