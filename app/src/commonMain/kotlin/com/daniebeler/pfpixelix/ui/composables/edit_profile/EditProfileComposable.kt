package com.daniebeler.pfpixelix.ui.composables.edit_profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileComposable(
    navController: NavController,
    viewModel: EditProfileViewModel = rememberViewModel(key = "edit-profile-viewmodel-key") { editProfileViewModel }
) {

    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalKmpContext.current
//todo
//    val imageCropLauncher = rememberLauncherForActivityResult(CropImageContract()) { result ->
//        if (result.isSuccessful) {
//            // use the cropped image
//            if (result.uriContent != null) {
//                viewModel.avatarUri = result.uriContent!!
//                viewModel.avatarChanged = true
//            }
//        } else {
//            // an error occurred cropping
//            println(result.error)
//        }
//    }
//
//
//    val singlePhotoPickerLauncher = rememberLauncherForActivityResult(
//        contract = ActivityResultContracts.PickVisualMedia(),
//        onResult = { uri ->
//            if (uri != null) {
//                val cropOptions = CropImageContractOptions(
//                    uri, CropImageOptions(
//                        fixAspectRatio = true,
//                        aspectRatioX = 1,
//                        aspectRatioY = 1,
//                        cropShape = CropImageView.CropShape.OVAL
//                    )
//                )
//                imageCropLauncher.launch(cropOptions)
//            }
//        })

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(scrollBehavior = scrollBehavior,
                title = {
                    Text(text = stringResource(Res.string.edit_profile), fontWeight = FontWeight.Bold)
                },
                navigationIcon = {
                    IconButton(onClick = {
                        navController.popBackStack()
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Rounded.ArrowBackIos,
                            contentDescription = ""
                        )
                    }
                },
                actions = {
                    if (viewModel.firstLoaded) {
                        if (viewModel.displayname == (viewModel.accountState.account?.displayname
                                ?: "") && viewModel.note == (viewModel.accountState.account?.note
                                ?: "") && "https://" + viewModel.website == (viewModel.accountState.account?.website
                                ?: "") && !viewModel.avatarChanged && viewModel.privateProfile == viewModel.accountState.account?.locked
                        ) {
                            if (!viewModel.accountState.isLoading) {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.width(120.dp),
                                    shape = RoundedCornerShape(12.dp),
                                    enabled = false,
                                    colors = ButtonDefaults.buttonColors(
                                        disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                        disabledContentColor = MaterialTheme.colorScheme.onSurface
                                    )
                                ) {
                                    Text(text = stringResource(Res.string.save))
                                }
                            }
                        } else {
                            if (viewModel.accountState.isLoading) {
                                Button(
                                    onClick = {},
                                    modifier = Modifier.width(120.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    CircularProgressIndicator(
                                        modifier = Modifier.size(20.dp),
                                        color = MaterialTheme.colorScheme.onPrimary
                                    )
                                }
                            } else {
                                Button(
                                    onClick = {
                                        viewModel.save(context)
                                    },
                                    modifier = Modifier.width(120.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Text(text = stringResource(Res.string.save))
                                }
                            }
                        }
                    }
                })
        }) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            Column(
                Modifier
                    .fillMaxSize()
                    .padding(12.dp)
                    .verticalScroll(state = rememberScrollState())
            ) {

                if (viewModel.accountState.account != null) {

                    Box(modifier = Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        //todo
//                        AsyncImage(model = viewModel.avatarUri,
//                            contentDescription = "",
//                            modifier = Modifier
//                                .height(112.dp)
//                                .width(112.dp)
//                                .clip(CircleShape)
//                                .clickable {
////                                    singlePhotoPickerLauncher.launch(
////                                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly)
////                                    )
//                                })
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    Row {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.displayname),
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(Modifier.height(6.dp))

                    TextField(
                        value = viewModel.displayname,
                        singleLine = true,
                        onValueChange = { viewModel.displayname = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row {
                        Spacer(Modifier.width(6.dp))
                        Text(text = stringResource(Res.string.bio), fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(6.dp))


                    TextField(
                        value = viewModel.note,
                        onValueChange = { viewModel.note = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row {
                        Spacer(Modifier.width(6.dp))
                        Text(text = stringResource(Res.string.website), fontWeight = FontWeight.Bold)
                    }

                    Spacer(Modifier.height(6.dp))

                    TextField(
                        value = viewModel.website,
                        singleLine = true,
                        prefix = {
                            Text(text = "https://")
                        },
                        onValueChange = { viewModel.website = it },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = TextFieldDefaults.colors(
                            unfocusedIndicatorColor = Color.Transparent,
                            focusedIndicatorColor = Color.Transparent,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer
                        )
                    )

                    Spacer(modifier = Modifier.height(18.dp))

                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Spacer(Modifier.width(6.dp))
                        Text(
                            text = stringResource(Res.string.private_profile),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.weight(1f))
                        Switch(checked = viewModel.privateProfile,
                            onCheckedChange = { viewModel.privateProfile = it })
                    }

                }

            }
        }

    }
}