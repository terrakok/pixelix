package com.daniebeler.pfpixelix.ui.composables.collection

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.ButtonRowElement
import com.daniebeler.pfpixelix.ui.composables.InfinitePostsGrid
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.ui.composables.states.EmptyState
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import com.daniebeler.pfpixelix.utils.Share
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CollectionComposable(
    navController: NavController,
    collectionId: String,
    viewModel: CollectionViewModel = rememberViewModel(key = "collection-viewmodel-key") { collectionViewModel }
) {

    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val showAddPostBottomSheetState = rememberModalBottomSheetState(skipPartiallyExpanded = false)
    var showBottomSheet by remember { mutableStateOf(false) }
    var showAddPostBottomSheet by remember { mutableStateOf(false) }

    val context = LocalKmpContext.current

    LaunchedEffect(Unit) {
        viewModel.loadData(collectionId)
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        CenterAlignedTopAppBar(title = {
            if (viewModel.collectionState.collection != null) {
                if (viewModel.editState.editMode) {
                    TextField(
                        value = viewModel.editState.name,
                        singleLine = true,
                        onValueChange = { viewModel.editState = viewModel.editState.copy(name = it) },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    )
                } else {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            viewModel.collectionState.collection!!.title, fontWeight = FontWeight.Bold
                        )
                        Text(
                            stringResource(
                                Res.string.by, viewModel.collectionState.collection!!.username
                            ), fontSize = 12.sp, lineHeight = 6.sp
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
        }, actions = {
            if (viewModel.editState.editMode) {
                TextButton(onClick = {
                    viewModel.toggleEditMode()
                }) {
                    Text(stringResource(Res.string.cancel))
                }
                TextButton(onClick = {
                    viewModel.confirmEdit()
                }) {
                    Text(stringResource(Res.string.confirm))
                }
            } else {

                IconButton(onClick = {
                    viewModel.toggleEditMode()
                }) {
                    Icon(
                        imageVector = Icons.Outlined.Edit, contentDescription = ""
                    )
                }
                IconButton(onClick = {
                    //Navigate.navigate("settings_screen", navController)
                    showBottomSheet = true
                }) {
                    Icon(
                        imageVector = Icons.Outlined.MoreVert, contentDescription = ""
                    )
                }
            }
        })
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            InfinitePostsGrid(items = if (viewModel.editState.editMode) {
                viewModel.editState.editPosts
            } else {
                viewModel.collectionPostsState.posts
            },
                isLoading = viewModel.collectionPostsState.isLoading,
                isRefreshing = viewModel.collectionPostsState.isRefreshing,
                error = viewModel.collectionPostsState.error,
                emptyMessage = EmptyState(
                    icon = Icons.Outlined.FavoriteBorder, heading = "Empty Collection"
                ),
                navController = navController,
                getItemsPaginated = {
                    //viewModel.getItemsPaginated()
                },
                after = {
                    if (viewModel.editState.editMode) {
                        Spacer(Modifier.height(22.dp))
                        IconButton(onClick = {
                            showAddPostBottomSheet = true
                            viewModel.getPostsExceptCollection()
                        }) {
                            Icon(
                                Icons.Outlined.AddCircle,
                                contentDescription = "",
                                Modifier.height(200.dp).width(200.dp)
                            )
                        }
                    }

                },
                onRefresh = {
                    viewModel.refresh()
                },
                edit = viewModel.editState.editMode,
                editRemove = { id -> viewModel.editRemove(id) })
        }

        if (showBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showBottomSheet = false
                },
                sheetState = sheetState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {

                    ButtonRowElement(icon = Icons.Outlined.OpenInBrowser, text = stringResource(
                        Res.string.open_in_browser
                    ), onClick = {
                        if (viewModel.collectionState.collection != null) {
                            viewModel.openUrl(
                                context, viewModel.collectionState.collection!!.url
                            )
                        }
                    })

                    ButtonRowElement(icon = Icons.Outlined.Share,
                        text = stringResource(Res.string.share_this_collection),
                        onClick = {
                            if (viewModel.collectionState.collection != null) {
                                Share.shareText(context, viewModel.collectionState.collection!!.url)
                            }
                        })
                }

            }

        }
        if (showAddPostBottomSheet) {
            ModalBottomSheet(
                onDismissRequest = {
                    showAddPostBottomSheet = false
                },
                sheetState = showAddPostBottomSheetState,
                modifier = Modifier.windowInsetsPadding(WindowInsets.navigationBars)
            ) {
                Column(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    InfinitePostsGrid(items = viewModel.editState.allPostsExceptCollection,
                        isLoading = viewModel.editState.isLoading,
                        isRefreshing = false,
                        error = viewModel.editState.error,
                        emptyMessage = EmptyState(
                            icon = Icons.Outlined.FavoriteBorder, heading = "Empty Collection"
                        ),
                        navController = navController,
                        getItemsPaginated = {
                            //viewModel.getItemsPaginated()
                        },
                        onRefresh = {
                            viewModel.refresh()
                        }, onClick = { viewModel.addPostToCollection(it) }, pullToRefresh = false
                    )
                }
            }
        }
    }
}