package com.daniebeler.pfpixelix.ui.composables.settings.preferences

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.automirrored.rounded.ArrowBackIos
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.ui.composables.ButtonRowElement
import com.daniebeler.pfpixelix.ui.composables.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.SwitchRowItem
import com.daniebeler.pfpixelix.ui.composables.ThemeViewModel
import com.daniebeler.pfpixelix.ui.composables.rememberViewModel
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import com.daniebeler.pfpixelix.utils.Navigate
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesComposable(
    navController: NavController,
    themeViewModel: ThemeViewModel = rememberViewModel(key = "Theme") { this.themeViewModel },
    viewModel: PreferencesViewModel = rememberViewModel(key = "preferences-viewmodel-key") { preferencesViewModel }
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()

    val context = LocalKmpContext.current

    val showLogoutDialog = remember { mutableStateOf(false) }
    val showThemeDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        getCacheSize(context, viewModel)
        viewModel.getVersionName(context)
        viewModel.getAppIcon(context)
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(scrollBehavior = scrollBehavior,
                title = {
                    Text(text = stringResource(Res.string.settings), fontWeight = FontWeight.Bold)
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
                })
        }) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState())
        ) {
            SwitchRowItem(
                Icons.Outlined.NoAdultContent,
                stringResource(Res.string.hide_sensitive_content),
                viewModel.isSensitiveContentHidden
            ) { checked -> viewModel.storeHideSensitiveContent(checked) }

            SwitchRowItem(
                Icons.Outlined.Description,
                stringResource(Res.string.hide_alt_text_button),
                viewModel.isAltTextButtonHidden
            ) { checked -> viewModel.storeHideAltTextButton(checked) }

            SwitchRowItem(
                Icons.Outlined.OpenInBrowser,
                stringResource(Res.string.use_in_app_browser),
                viewModel.isUsingInAppBrowser
            ) { checked -> viewModel.storeUseInAppBrowser(checked) }

            HorizontalDivider(modifier = Modifier.padding(12.dp))

            ButtonRowElement(icon = Icons.Outlined.Palette,
                text = stringResource(Res.string.app_theme),
                smallText = getThemeString(themeViewModel.currentTheme.theme),
                onClick = { showThemeDialog.value = true })

            if (viewModel.appIcon == null) {
                ButtonRowElement(
                    icon = Res.drawable.pixelix_logo,
                    text = stringResource(Res.string.customize_app_icon),
                    onClick = {
                        Navigate.navigate("icon_selection_screen", navController)
                    })
            } else {
                ButtonRowElement(
                    icon = viewModel.appIcon!!,
                    text = stringResource(Res.string.customize_app_icon),
                    onClick = {
                        Navigate.navigate("icon_selection_screen", navController)
                    })
            }

            HorizontalDivider(modifier = Modifier.padding(12.dp))

            ButtonRowElement(icon = Icons.Outlined.Save,
                text = stringResource(Res.string.clear_cache),
                smallText = viewModel.cacheSize,
                onClick = {
                    deleteCache(context, viewModel = viewModel)
                })

            ButtonRowElement(icon = Icons.Outlined.Settings,
                text = stringResource(Res.string.more_settings),
                onClick = {
                    viewModel.openMoreSettingsPage(context)
                })

            ButtonRowElement(
                icon = Icons.AutoMirrored.Outlined.Logout, text = stringResource(
                    Res.string.logout
                ), onClick = {
                    showLogoutDialog.value = true
                }, color = MaterialTheme.colorScheme.error
            )


            HorizontalDivider(modifier = Modifier.padding(12.dp))

            Text(
                text = "Pixelix v" + viewModel.versionName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }

        if (showLogoutDialog.value) {
            AlertDialog(title = {
                Text(text = stringResource(Res.string.logout_questionmark))
            }, text = {
                Text(text = stringResource(Res.string.are_you_sure_you_want_to_log_out))
            }, onDismissRequest = {
                showLogoutDialog.value = false
            }, confirmButton = {
                val contextNavigation = LocalAppComponent.current.contextNavigation
                TextButton(onClick = {
                    CoroutineScope(Dispatchers.Default).launch {
                        viewModel.logout()
                        contextNavigation.gotoLoginActivity(false)
                    }
                }) {
                    Text(stringResource(Res.string.logout))
                }
            }, dismissButton = {
                TextButton(onClick = {
                    showLogoutDialog.value = false
                }) {
                    Text(stringResource(Res.string.cancel))
                }
            })
        }

        if (showThemeDialog.value) {
            val themeOptions = listOf("system", "dark", "light")
            AlertDialog(title = {
                Text(text = stringResource(Res.string.app_theme))
            }, text = {
                Column(Modifier.selectableGroup()) {
                    themeOptions.forEach { text ->
                        Row(
                            Modifier
                                .fillMaxWidth()
                                .height(56.dp)
                                .selectable(
                                    selected = (text == themeViewModel.currentTheme.theme),
                                    onClick = {
                                        showThemeDialog.value = false
                                        themeViewModel.storeTheme(text)
                                    },
                                    role = Role.RadioButton
                                )
                                .padding(horizontal = 16.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = (text == themeViewModel.currentTheme.theme),
                                onClick = null
                            )
                            Text(
                                text = getThemeString(text),
                                style = MaterialTheme.typography.bodyLarge,
                                modifier = Modifier.padding(start = 16.dp)
                            )
                        }
                    }
                }
            }, onDismissRequest = {
                showThemeDialog.value = false
            }, confirmButton = {}, dismissButton = {
                TextButton(onClick = {
                    showThemeDialog.value = false
                }) {
                    Text(stringResource(Res.string.cancel))
                }
            })
        }
    }
}

@Composable
private fun getThemeString(theme: String): String {
    return when (theme) {
        "system" -> stringResource(Res.string.theme_system)
        "dark" -> stringResource(Res.string.theme_dark)
        "light" -> stringResource(Res.string.theme_light)
        else -> ""
    }
}


private fun getCacheSize(context: KmpContext, settingsViewModel: PreferencesViewModel) {
    //todo
//    val cacheInbytes: Long =
//        KmpContext.cacheDir.walkBottomUp().fold(0L, { acc, file -> acc + file.length() })
//
//    settingsViewModel.cacheSize = humanReadableByteCountSI(cacheInbytes)
}

private fun humanReadableByteCountSI(bytes: Long): String {
    //todo
//    var bytes = bytes
//    if (-1000 < bytes && bytes < 1000) {
//        return "$bytes B"
//    }
//    val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
//    while (bytes <= -999950 || bytes >= 999950) {
//        bytes /= 1000
//        ci.next()
//    }
//    return String.format("%.1f %cB", bytes / 1000.0, ci.current())
    return "TODO"
}


private fun deleteCache(context: KmpContext, viewModel: PreferencesViewModel) {
    //todo
//    KmpContext.cacheDir.deleteRecursively()
//    getCacheSize(context, viewModel)
}