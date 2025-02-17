package com.daniebeler.pfpixelix.ui.composables.settings.preferences

import androidx.compose.foundation.OverscrollEffect
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBars
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.DrawerState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.input.nestedscroll.NestedScrollSource
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Velocity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.daniebeler.pfpixelix.di.injectViewModel
import com.daniebeler.pfpixelix.domain.service.platform.PlatformFeatures
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.ClearCachePref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.CustomizeAppIconPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.FocusModePref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.HideAltTextButtonPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.HideSensitiveContentPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.LogoutPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.MoreSettingsPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.RepostSettingsPref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.ThemePref
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.prefs.UseInAppBrowserPref
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.close_outline
import pixelix.app.generated.resources.pixelix_logo
import pixelix.app.generated.resources.settings

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PreferencesComposable(
    navController: NavController,
    drawerState: DrawerState,
    closePreferencesDrawer: () -> Unit,
    viewModel: PreferencesViewModel = injectViewModel(key = "preferences-viewmodel-key") { preferencesViewModel }
) {
    val scrollBehavior = TopAppBarDefaults.pinnedScrollBehavior()
    val context = LocalKmpContext.current

    LaunchedEffect(Unit) {
        viewModel.getVersionName(context)
        viewModel.getAppIcon(context)
    }

    Scaffold(contentWindowInsets = WindowInsets.systemBars.only(WindowInsetsSides.Top),
        modifier = Modifier.nestedScroll(scrollBehavior.nestedScrollConnection),
        topBar = {
            CenterAlignedTopAppBar(scrollBehavior = scrollBehavior, title = {
                Text(text = stringResource(Res.string.settings), fontWeight = FontWeight.Bold)
            }, navigationIcon = {
                IconButton(onClick = {
                    closePreferencesDrawer()
                }) {
                    Icon(
                        imageVector = vectorResource(Res.drawable.close_outline),
                        contentDescription = ""
                    )
                }
            })
        }) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = 18.dp)
                .padding(bottom = 18.dp)
                .fillMaxSize()
                .verticalScroll(state = rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {

            HideSensitiveContentPref()

            HideAltTextButtonPref()

            FocusModePref()

            UseInAppBrowserPref()

            RepostSettingsPref { viewModel.openRepostSettings(context) }

            HorizontalDivider(modifier = Modifier.padding(12.dp))

            ThemePref()

            if (PlatformFeatures.customAppIcon) {
                if (viewModel.appIcon == null) {
                    CustomizeAppIconPref(
                        navController,
                        closePreferencesDrawer,
                        Res.drawable.pixelix_logo
                    )
                } else {
                    CustomizeAppIconPref(navController, closePreferencesDrawer, viewModel.appIcon!!)
                }
            }

            HorizontalDivider(modifier = Modifier.padding(12.dp))

            ClearCachePref(drawerState)

            MoreSettingsPref { viewModel.openMoreSettingsPage(context) }

            LogoutPref { viewModel.logout() }

            HorizontalDivider(modifier = Modifier.padding(12.dp))

            Text(
                text = "Pixelix v" + viewModel.versionName,
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                fontSize = 12.sp
            )
        }
    }
}