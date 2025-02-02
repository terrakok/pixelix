package com.daniebeler.pfpixelix.ui.composables.newpost

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.outlined.ArrowDownward
import androidx.compose.material.icons.outlined.ArrowUpward
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.common.Constants.AUDIENCE_FOLLOWERS_ONLY
import com.daniebeler.pfpixelix.common.Constants.AUDIENCE_PUBLIC
import com.daniebeler.pfpixelix.common.Constants.AUDIENCE_UNLISTED
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.ui.composables.states.ErrorComposable
import com.daniebeler.pfpixelix.ui.composables.states.LoadingComposable
import com.daniebeler.pfpixelix.ui.composables.textfield_location.TextFieldLocationsComposable
import com.daniebeler.pfpixelix.ui.composables.textfield_mentions.TextFieldMentionsComposable
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import com.daniebeler.pfpixelix.utils.MimeType
import com.daniebeler.pfpixelix.utils.imeAwareInsets
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewPostComposable(
    navController: NavController,
    viewModel: NewPostViewModel = rememberViewModel(key = "new-post-viewmodel-key") { newPostViewModel }
) {
    val context = LocalKmpContext.current
//todo
//    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickMultipleVisualMedia(),
//        onResult = { uris ->
//            Navigate.navigate("new_post_screen", navController)
//            uris.forEach {
//                viewModel.addImage(it, context)
//            }
//        })

    var expanded by remember { mutableStateOf(false) }
    var showReleaseAlert by remember {
        mutableStateOf(false)
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        TopAppBar(title = {
            Text(text = stringResource(Res.string.new_post), fontWeight = FontWeight.Bold)
        }, navigationIcon = {
            IconButton(onClick = {
                navController.popBackStack()
            }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos, contentDescription = ""
                )
            }
        }, actions = {
            Button(
                onClick = { showReleaseAlert = true },
                enabled = (viewModel.images.isNotEmpty() && !viewModel.mediaUploadState.isLoading)
            ) {
                Text(text = stringResource(Res.string.release))
            }
        })
    }) { paddingValues ->
        Box {
            Column(
                Modifier
                    .padding(paddingValues)
                    .imeAwareInsets(90.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(12.dp), verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                viewModel.images.forEachIndexed { index, image ->
                    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                        Box(contentAlignment = Alignment.Center) {

                            val type = MimeType.getMimeType(image.imageUri, context)
                            if (type != null && type.take(5) == "video") {
                                //todo
//                                val model = ImageRequest.Builder(context).data(image.imageUri)
//                                    .decoderFactory { result, options, _ ->
//                                        VideoFrameDecoder(
//                                            result.source, options
//                                        )
//                                    }.build()
//
//                                AsyncImage(
//                                    model = model,
//                                    contentDescription = "video thumbnail",
//                                    modifier = Modifier.width(100.dp)
//                                )
                            } else {
                                AsyncImage(
                                    model = image.imageUri,
                                    contentDescription = null,
                                    modifier = Modifier.width(100.dp)
                                )
                            }
                            if (image.isLoading) {
                                CircularProgressIndicator(
                                    modifier = Modifier.wrapContentSize(Alignment.Center)
                                )
                            }
                        }
                        Spacer(Modifier.width(10.dp))

                        OutlinedTextField(
                            value = image.text,
                            onValueChange = { viewModel.updateAltTextVariable(index, it) },
                            modifier = Modifier.weight(1f),
                            label = { Text(stringResource(Res.string.alt_text)) },
                        )

                        if (viewModel.images.size > 1) {
                            Column {
                                IconButton(onClick = { viewModel.moveMediaAttachmentUp(index) }) {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowUpward,
                                        contentDescription = "move Imageupwards"
                                    )
                                }
                                IconButton(onClick = { viewModel.moveMediaAttachmentDown(index) }) {
                                    Icon(
                                        imageVector = Icons.Outlined.ArrowDownward,
                                        contentDescription = "move Imageupwards"
                                    )
                                }
                            }
                        }
                        IconButton(onClick = {
                            viewModel.deleteMedia(image.id, image.imageUri)
                        }) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "delete Image",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }

                    }

                }
                Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                    Icon(
                        modifier = Modifier
                            .clickable {
                                //todo
//                                singlePhotoPickerLauncher.launch(
//                                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageAndVideo)
//                                )
                            }
                            .height(50.dp)
                            .width(50.dp),
                        imageVector = Icons.Filled.Add,
                        contentDescription = null,
                    )
                }
                Spacer(modifier = Modifier.height(20.dp))
                TextFieldMentionsComposable(
                    submit = {},
                    text = viewModel.caption,
                    changeText = { text -> viewModel.caption = text },
                    labelStringId = Res.string.caption,
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Default,
                    suggestionsBoxColor = MaterialTheme.colorScheme.surfaceContainer,
                    submitButton = null
                )/*OutlinedTextField(
                    value = viewModel.caption,
                    onValueChange = { viewModel.caption = it },
                    modifier = Modifier.fillMaxWidth(),
                    label = { Text(stringResource(Res.string.caption)) },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                        unfocusedBorderColor = Color.Transparent,
                    ),
                    shape = RoundedCornerShape(12.dp),
                )*/
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(Res.string.sensitive_nsfw_media))
                    Switch(checked = viewModel.sensitive,
                        onCheckedChange = { viewModel.sensitive = it })
                }
                if (viewModel.sensitive) {
                    OutlinedTextField(
                        value = viewModel.sensitiveText,
                        onValueChange = { viewModel.sensitiveText = it },
                        modifier = Modifier.fillMaxWidth(),
                        label = { Text(stringResource(Res.string.content_warning_or_spoiler_text)) },
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(text = stringResource(Res.string.audience))
                    Box {
                        OutlinedButton(onClick = { expanded = !expanded }) {
                            val buttonText: String = when (viewModel.audience) {
                                AUDIENCE_PUBLIC -> {
                                    stringResource(Res.string.audience_public)
                                }

                                AUDIENCE_UNLISTED -> {
                                    stringResource(Res.string.unlisted)
                                }

                                AUDIENCE_FOLLOWERS_ONLY -> {
                                    stringResource(Res.string.followers_only)
                                }

                                else -> {
                                    ""
                                }
                            }
                            Text(text = buttonText)
                        }
                        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                            DropdownMenuItem(text = { Text(stringResource(Res.string.audience_public)) },
                                onClick = { viewModel.audience = AUDIENCE_PUBLIC },
                                trailingIcon = {
                                    if (viewModel.audience == AUDIENCE_PUBLIC) {
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                })
                            DropdownMenuItem(text = { Text(stringResource(Res.string.unlisted)) },
                                onClick = { viewModel.audience = AUDIENCE_UNLISTED },
                                trailingIcon = {
                                    if (viewModel.audience == AUDIENCE_UNLISTED) {
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                })
                            DropdownMenuItem(text = { Text(stringResource(Res.string.followers_only)) },
                                onClick = { viewModel.audience = AUDIENCE_FOLLOWERS_ONLY },
                                trailingIcon = {
                                    if (viewModel.audience == AUDIENCE_FOLLOWERS_ONLY) {
                                        Icon(
                                            imageVector = Icons.Outlined.Check,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.primary
                                        )
                                    }
                                })
                        }



                    }
                }
                TextFieldLocationsComposable(
                    submit = {viewModel.setLocation(it)},
                    submitPlace = {},
                    initialValue = null,
                    labelStringId = Res.string.location,
                    modifier = Modifier.fillMaxWidth(),
                    imeAction = ImeAction.Default,
                    suggestionsBoxColor = MaterialTheme.colorScheme.surfaceContainer,
                    submitButton = null
                )
            }

            if (viewModel.addImageError.first.isNotBlank()) {
                AlertDialog(title = {
                    Text(text = viewModel.addImageError.first)
                }, text = {
                    Text(text = viewModel.addImageError.second)
                }, onDismissRequest = {
                    viewModel.addImageError = Pair("", "")
                }, confirmButton = {
                    TextButton(onClick = {
                        viewModel.addImageError = Pair("", "")
                    }) {
                        Text("Ok")
                    }
                })
            }

            if (showReleaseAlert) {
                AlertDialog(title = {
                    Text(text = "Are you sure?")
                }, onDismissRequest = {
                    showReleaseAlert = false
                }, dismissButton = {
                    TextButton(onClick = {
                        showReleaseAlert = false
                    }) {
                        Text(stringResource(Res.string.cancel))
                    }
                }, confirmButton = {
                    TextButton(onClick = {
                        showReleaseAlert = false
                        viewModel.post(navController)
                    }) {
                        Text(stringResource(Res.string.release))
                    }
                })
            }

            LoadingComposable(isLoading = viewModel.createPostState.isLoading)
            //LoadingComposable(isLoading = viewModel.mediaUploadState.isLoading)
            ErrorComposable(message = viewModel.mediaUploadState.error)
            ErrorComposable(message = viewModel.createPostState.error)
        }
    }
}