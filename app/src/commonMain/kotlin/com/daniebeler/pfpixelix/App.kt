package com.daniebeler.pfpixelix

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.shapes
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberDrawerState
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.ReverseModalNavigationDrawer
import com.daniebeler.pfpixelix.ui.composables.profile.own_profile.AccountSwitchBottomSheet
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.PreferencesComposable
import com.daniebeler.pfpixelix.ui.navigation.Destination
import com.daniebeler.pfpixelix.ui.navigation.navigationGraph
import com.daniebeler.pfpixelix.ui.theme.PixelixTheme
import com.daniebeler.pfpixelix.utils.end
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.StringResource
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.resources.vectorResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.add_circle
import pixelix.app.generated.resources.add_circle_outline
import pixelix.app.generated.resources.bookmark_outline
import pixelix.app.generated.resources.default_avatar
import pixelix.app.generated.resources.home
import pixelix.app.generated.resources.house
import pixelix.app.generated.resources.house_fill
import pixelix.app.generated.resources.new_post
import pixelix.app.generated.resources.notifications
import pixelix.app.generated.resources.notifications_outline
import pixelix.app.generated.resources.profile
import pixelix.app.generated.resources.search
import pixelix.app.generated.resources.search_outline

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun App(
    appComponent: AppComponent,
    exitApp: () -> Unit
) {
    val uriHandler = LocalUriHandler.current
    DisposableEffect(uriHandler) {
        val systemUrlHandler = appComponent.systemUrlHandler
        systemUrlHandler.uriHandler = uriHandler
        onDispose {
            systemUrlHandler.uriHandler = null
        }
    }
    CompositionLocalProvider(
        LocalAppComponent provides appComponent
    ) {
        PixelixTheme {
            val navController = rememberNavController()
            val scope = rememberCoroutineScope()
            val drawerState = rememberDrawerState(DrawerValue.Closed)
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            var showAccountSwitchBottomSheet by remember { mutableStateOf(false) }

            var activeUser by remember { mutableStateOf<String?>("unknown") }
            LaunchedEffect(Unit) {
                val authService = appComponent.authService
                authService.openSessionIfExist()
                authService.activeUser.collect {
                    activeUser = it
                }
            }
            if (activeUser == "unknown") return@PixelixTheme

            ReverseModalNavigationDrawer(
                gesturesEnabled = drawerState.isOpen,
                drawerState = drawerState,
                drawerContent = {
                    ModalDrawerSheet(
                        drawerState = drawerState,
                        drawerShape = shapes.extraLarge.end(0.dp),
                    ) {
                        PreferencesComposable(navController, drawerState, {
                            scope.launch {
                                drawerState.close()
                            }
                        })
                    }
                }
            ) {

                Scaffold(
                    contentWindowInsets = WindowInsets(0),
                    bottomBar = {
                        BottomBar(
                            navController = navController,
                            openAccountSwitchBottomSheet = {
                                showAccountSwitchBottomSheet = true
                            },
                        )
                    },
                    content = { paddingValues ->
                        NavHost(
                            modifier = Modifier.fillMaxSize().padding(paddingValues)
                                .consumeWindowInsets(WindowInsets.navigationBars),
                            navController = navController,
                            startDestination = Destination.FirstLogin,
                            builder = {
                                navigationGraph(
                                    navController,
                                    { scope.launch { drawerState.open() } },
                                    exitApp
                                )
                            }
                        )
                        LaunchedEffect(activeUser) {
                            val rootScreen =
                                if (activeUser == null) Destination.FirstLogin else Destination.Feeds
                            navController.navigate(rootScreen) {
                                val root = navController.currentBackStack.value
                                    .firstOrNull { it.destination.route != null }
                                    ?.destination?.route
                                if (root != null) {
                                    popUpTo(root) { inclusive = true }
                                }
                            }

                            if (activeUser != null) {
                                appComponent.systemFileShare.shareFilesRequests.collect { uris ->
                                    navController.navigate(
                                        Destination.NewPost(uris.map { it.toString() })
                                    )
                                }
                            }
                        }
                    }
                )
            }
            if (showAccountSwitchBottomSheet) {
                ModalBottomSheet(
                    onDismissRequest = {
                        showAccountSwitchBottomSheet = false
                    }, sheetState = sheetState
                ) {
                    AccountSwitchBottomSheet(
                        navController = navController,
                        closeBottomSheet = { showAccountSwitchBottomSheet = false },
                        null
                    )
                }
            }
        }
    }
}

private enum class HomeTab(
    val destination: Destination,
    val icon: DrawableResource,
    val activeIcon: DrawableResource,
    val label: StringResource
) {
    Feeds(Destination.Feeds, Res.drawable.house, Res.drawable.house_fill, Res.string.home),
    Search(
        Destination.Search(),
        Res.drawable.search_outline,
        Res.drawable.search,
        Res.string.search
    ),
    NewPost(
        Destination.NewPost(),
        Res.drawable.add_circle_outline,
        Res.drawable.add_circle,
        Res.string.new_post
    ),
    Notifications(
        Destination.Notifications,
        Res.drawable.notifications_outline,
        Res.drawable.notifications,
        Res.string.notifications
    ),
    OwnProfile(
        Destination.OwnProfile,
        Res.drawable.bookmark_outline,
        Res.drawable.bookmark_outline,
        Res.string.profile
    )
}

@Composable
private fun BottomBar(
    navController: NavHostController,
    openAccountSwitchBottomSheet: () -> Unit
) {
    val systemNavigationBarHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    var avatar by remember { mutableStateOf<String?>(null) }
    val appComponent = LocalAppComponent.current
    LaunchedEffect(Unit) {
        val authService = appComponent.authService
        authService.activeUser
            .map { authService.getCurrentSession() }
            .collect {
                avatar = it?.avatar
            }
    }

    NavigationBar(
        modifier = Modifier.height(60.dp + systemNavigationBarHeight)
    ) {
        val currentStack by navController.currentBackStack.collectAsState()

        HomeTab.entries.forEach { tab ->
            val isSelected = currentStack.any { it.destination.hasRoute(tab.destination::class) }
            val isCurrentDestination =
                currentStack.lastOrNull()?.destination?.hasRoute(tab.destination::class) == true

            val interactionSource = remember { MutableInteractionSource() }
            val coroutineScope = rememberCoroutineScope()
            var isLongPress by remember { mutableStateOf(false) }

            LaunchedEffect(interactionSource) {
                interactionSource.interactions.collect { interaction ->
                    when (interaction) {
                        is PressInteraction.Press -> {
                            isLongPress = false // Reset flag before starting detection
                            coroutineScope.launch {
                                delay(500L) // Long-press threshold
                                if (tab == HomeTab.OwnProfile) {
                                    openAccountSwitchBottomSheet()
                                }
                                isLongPress = true
                            }
                        }

                        is PressInteraction.Release, is PressInteraction.Cancel -> {
                            coroutineScope.coroutineContext.cancelChildren()
                        }
                    }
                }
            }
            NavigationBarItem(
                icon = {
                    if (tab == HomeTab.OwnProfile && avatar != null) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            AsyncImage(
                                model = avatar,
                                error = painterResource(Res.drawable.default_avatar),
                                contentDescription = "",
                                modifier = Modifier
                                    .height(30.dp)
                                    .width(30.dp)
                                    .clip(CircleShape)
                            )
                            Icon(
                                Icons.Outlined.UnfoldMore,
                                contentDescription = "long press to switch account"
                            )
                        }
                    } else {
                        Icon(
                            imageVector = vectorResource(
                                if (isSelected) tab.activeIcon else tab.icon
                            ),
                            modifier = Modifier.size(30.dp),
                            contentDescription = stringResource(tab.label)
                        )
                    }
                },
                selected = isSelected,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.inverseSurface,
                    indicatorColor = Color.Transparent
                ),
                interactionSource = interactionSource,
                onClick = {
                    if (!isLongPress && !isCurrentDestination) {
                        navController.navigate(tab.destination) {
                            if (!isSelected) {
                                popUpTo(0) {
                                    saveState = true
                                }
                                restoreState = true
                            } else {
                                popUpTo(0)
                            }
                            launchSingleTop = true
                        }
                    }
                })
        }
    }
}

//https://partnerissuetracker.corp.google.com/issues/246909281
@Composable
expect fun EdgeToEdgeDialog(
    onDismissRequest: () -> Unit,
    properties: DialogProperties,
    content: @Composable () -> Unit
)
