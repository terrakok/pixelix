package com.daniebeler.pfpixelix.ui.composables.settings.icon_selection

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.*


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun IconSelectionComposable(
    navController: NavController,
    viewModel: IconSelectionViewModel = rememberViewModel(key = "iconselectionvm") { iconSelectionViewModel }
) {

    val context = LocalKmpContext.current

    val newIconName = remember { mutableStateOf("") }

    val lazyGridState = rememberLazyGridState()

    LaunchedEffect(Unit) {
        viewModel.fillList(context)
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top), topBar = {
        CenterAlignedTopAppBar(title = {
            Text("Icon Selection", fontWeight = FontWeight.Bold)
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

            LazyVerticalGrid(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 12.dp),
                state = lazyGridState,
                columns = GridCells.Fixed(3)
            ) {
                item (span = { GridItemSpan(3) }) {
                    Column {
                        Row {
                            Text(text = stringResource(Res.string.two_icons_info))
                        }

                        HorizontalDivider(Modifier.padding(vertical = 12.dp))
                    }

                }

                items(viewModel.icons) {
                    if (it.enabled) {
                        Box(
                            modifier = Modifier
                                .border(
                                    BorderStroke(2.dp, MaterialTheme.colorScheme.primary),
                                    shape = CircleShape
                                )
                                .padding(6.dp)
                        ) {
                            Image(
                                it.icon,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier.padding(6.dp)
                        ) {
                            Image(it.icon,
                                contentDescription = null,
                                contentScale = ContentScale.Crop,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .aspectRatio(1f)
                                    .clickable {
                                        newIconName.value = it.name
                                    })
                        }
                    }

                }
            }


        }

        if (newIconName.value.isNotBlank()) {
            AlertDialog(title = {
                Text(text = stringResource(Res.string.change_app_icon))
            }, text = {
                Text(text = stringResource(Res.string.change_app_icon_dialog_content))
            }, onDismissRequest = {
                newIconName.value = ""
            }, confirmButton = {
                TextButton(onClick = {
                    viewModel.changeIcon(context = context, newIconName.value)
                    newIconName.value = ""
                }) {
                    Text(stringResource(Res.string.change))
                }
            }, dismissButton = {
                TextButton(onClick = {
                    newIconName.value = ""
                }) {
                    Text(stringResource(Res.string.cancel))
                }
            })
        }

    }
}