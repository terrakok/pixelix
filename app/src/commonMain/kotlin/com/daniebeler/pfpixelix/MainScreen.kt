package com.daniebeler.pfpixelix

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.UnfoldMore
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import co.touchlab.kermit.Logger
import coil3.compose.AsyncImage
import com.daniebeler.pfpixelix.common.Destinations
import com.daniebeler.pfpixelix.di.AppComponent
import com.daniebeler.pfpixelix.di.HostSelectionInterceptorInterface
import com.daniebeler.pfpixelix.domain.model.LoginData
import com.daniebeler.pfpixelix.domain.repository.CountryRepository
import com.daniebeler.pfpixelix.domain.usecase.GetCurrentLoginDataUseCase
import com.daniebeler.pfpixelix.domain.usecase.VerifyTokenUseCase
import com.daniebeler.pfpixelix.ui.composables.HomeComposable
import com.daniebeler.pfpixelix.ui.composables.LocalAppComponent
import com.daniebeler.pfpixelix.ui.composables.collection.CollectionComposable
import com.daniebeler.pfpixelix.ui.composables.direct_messages.chat.ChatComposable
import com.daniebeler.pfpixelix.ui.composables.direct_messages.conversations.ConversationsComposable
import com.daniebeler.pfpixelix.ui.composables.edit_post.EditPostComposable
import com.daniebeler.pfpixelix.ui.composables.edit_profile.EditProfileComposable
import com.daniebeler.pfpixelix.ui.composables.explore.ExploreComposable
import com.daniebeler.pfpixelix.ui.composables.followers.FollowersMainComposable
import com.daniebeler.pfpixelix.ui.composables.mention.MentionComposable
import com.daniebeler.pfpixelix.ui.composables.newpost.NewPostComposable
import com.daniebeler.pfpixelix.ui.composables.notifications.NotificationsComposable
import com.daniebeler.pfpixelix.ui.composables.profile.other_profile.OtherProfileComposable
import com.daniebeler.pfpixelix.ui.composables.profile.own_profile.OwnProfileComposable
import com.daniebeler.pfpixelix.ui.composables.settings.about_instance.AboutInstanceComposable
import com.daniebeler.pfpixelix.ui.composables.settings.about_pixelix.AboutPixelixComposable
import com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts.BlockedAccountsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts.BookmarkedPostsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.followed_hashtags.FollowedHashtagsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.icon_selection.IconSelectionComposable
import com.daniebeler.pfpixelix.ui.composables.settings.liked_posts.LikedPostsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts.MutedAccountsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.PreferencesComposable
import com.daniebeler.pfpixelix.ui.composables.single_post.SinglePostComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline.HashtagTimelineComposable
import com.daniebeler.pfpixelix.ui.theme.PixelixTheme
import com.daniebeler.pfpixelix.utils.ContextNavigation
import com.daniebeler.pfpixelix.utils.KmpContext
import com.daniebeler.pfpixelix.utils.LocalKmpContext
import com.daniebeler.pfpixelix.utils.Navigate
import kotlinx.coroutines.cancelChildren
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import me.tatarka.inject.annotations.Inject
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.default_avatar

@Inject
class MainScreen(
    private val appComponent: AppComponent,
    private val contextNavigation: ContextNavigation,
    private val currentLoginDataUseCase: GetCurrentLoginDataUseCase,
    private val hostSelectionInterceptorInterface: HostSelectionInterceptorInterface,
    private val repository: CountryRepository,
    private val verifyTokenUseCase: VerifyTokenUseCase
) {
    var avatar = ""

    fun onCreate() {
        runBlocking {
            val loginData: LoginData? = currentLoginDataUseCase()
            if (loginData == null || loginData.accessToken.isBlank() || loginData.baseUrl.isBlank()) {
                val oldBaseurl: String? = repository.getAuthV1Baseurl().firstOrNull()
                val oldAccessToken: String? = repository.getAuthV1Token().firstOrNull()
                if (oldBaseurl != null && oldAccessToken != null && oldBaseurl.isNotBlank() && oldAccessToken.isNotBlank()) {
                    repository.deleteAuthV1Data()
                    contextNavigation.updateAuthToV2(oldBaseurl, oldAccessToken)
                } else {
                    contextNavigation.gotoLoginActivity(false)
                }
            } else {
                if (loginData.accessToken.isNotEmpty()) {
                    hostSelectionInterceptorInterface.setToken(loginData.accessToken)
                }
                if (loginData.baseUrl.isNotEmpty()) {
                    hostSelectionInterceptorInterface.setHost(
                        loginData.baseUrl.replace(
                            "https://", ""
                        )
                    )
                }
                avatar = loginData.avatar
            }
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun content(destination: String) {
        CompositionLocalProvider(
            LocalKmpContext provides appComponent.context,
            LocalAppComponent provides appComponent
        ) {
            val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
            var showAccountSwitchBottomSheet by remember { mutableStateOf(false) }

            PixelixTheme {
                val navController: NavHostController = rememberNavController()

                Scaffold(contentWindowInsets = WindowInsets(0.dp), bottomBar = {
                    BottomBar(
                        navController = navController,
                        avatar = avatar,
                        openAccountSwitchBottomSheet = { showAccountSwitchBottomSheet = true }
                    )
                }) { paddingValues ->
                    Box(
                        modifier = Modifier.padding(paddingValues)
                    ) {
                        NavigationGraph(
                            navController = navController,
                        )
                        if (destination.isNotBlank()) {
                            // Delay the navigation action to ensure the graph is set
                            LaunchedEffect(Unit) {
                                when (destination) {
//                                    StartNavigation.Notifications.toString() -> Navigate.navigate(
//                                        "notifications_screen", navController
//                                    )
//
//                                    StartNavigation.Profile.toString() -> {
//                                        val accountId: String = intent.extras?.getString(
//                                            KEY_DESTINATION_PARAM
//                                        ) ?: ""
//                                        if (accountId.isNotBlank()) {
//                                            Navigate.navigate(
//                                                "profile_screen/$accountId", navController
//                                            )
//                                        }
//                                    }
//
//                                    StartNavigation.Post.toString() -> {
//                                        val postId: String = intent.extras?.getString(
//                                            KEY_DESTINATION_PARAM
//                                        ) ?: ""
//                                        if (postId.isNotBlank()) {
//                                            Navigate.navigate(
//                                                "single_post_screen/$postId", navController
//                                            )
//
//                                        }
//                                    }
                                }
                            }
                        }
                    }


                }
                if (showAccountSwitchBottomSheet) {
                    ModalBottomSheet(
                        onDismissRequest = {
                            showAccountSwitchBottomSheet = false
                        }, sheetState = sheetState
                    ) {
//                        AccountSwitchBottomSheet(closeBottomSheet = {
//                            showAccountSwitchBottomSheet = false
//                        }, null)
                    }
                }
            }
        }
    }
}

@Composable
fun NavigationGraph(navController: NavHostController) {
    NavHost(navController,
        startDestination = Destinations.HomeScreen.route,
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }) {
        composable(Destinations.HomeScreen.route) {
            HomeComposable(navController)
        }

        composable(Destinations.NotificationsScreen.route) {
            NotificationsComposable(navController)
        }

        composable(Destinations.Profile.route) { navBackStackEntry ->
            val uId = navBackStackEntry.arguments?.getString("userid")

            uId?.let { id ->
                OtherProfileComposable(navController, userId = id, byUsername = null)

            }
        }

        composable(Destinations.ProfileByUsername.route) { navBackStackEntry ->
            val username = navBackStackEntry.arguments?.getString("username")

            username?.let {
                OtherProfileComposable(navController, userId = "", byUsername = it)
            }
        }

        composable(Destinations.Hashtag.route) { navBackStackEntry ->
            val uId = navBackStackEntry.arguments?.getString("hashtag")
            uId?.let { id ->
                HashtagTimelineComposable(navController, id)
            }
        }

        composable(Destinations.EditProfile.route) {
            EditProfileComposable(navController)
        }

        composable(Destinations.Preferences.route) {
            PreferencesComposable(navController)
        }

        composable(Destinations.IconSelection.route) {
            IconSelectionComposable(navController)
        }

        composable(Destinations.NewPost.route) {
            NewPostComposable(navController)
        }

        composable(Destinations.EditPost.route) { navBackStackEntry ->
            val postId = navBackStackEntry.arguments?.getString("postId")
            postId?.let { id ->
                EditPostComposable(postId, navController)
            }
        }

        composable(Destinations.MutedAccounts.route) {
            MutedAccountsComposable(navController)
        }

        composable(Destinations.BlockedAccounts.route) {
            BlockedAccountsComposable(navController)
        }

        composable(Destinations.LikedPosts.route) {
            LikedPostsComposable(navController)
        }

        composable(Destinations.BookmarkedPosts.route) {
            BookmarkedPostsComposable(navController)
        }

        composable(Destinations.FollowedHashtags.route) {
            FollowedHashtagsComposable(navController)
        }

        composable(Destinations.AboutInstance.route) {
            AboutInstanceComposable(navController)
        }

        composable(Destinations.AboutPixelix.route) {
            AboutPixelixComposable(navController)
        }

        composable(Destinations.OwnProfile.route) {
            OwnProfileComposable(navController)
        }

        composable(Destinations.Followers.route) { navBackStackEntry ->
            val uId = navBackStackEntry.arguments?.getString("userid")
            val page = navBackStackEntry.arguments?.getString("page")
            if (uId != null && page != null) {
                FollowersMainComposable(navController, accountId = uId, page = page)
            }
        }

        composable(
            "${Destinations.SinglePost.route}?refresh={refresh}&openReplies={openReplies}",
            arguments = listOf(navArgument("refresh") {
                defaultValue = false
            }, navArgument("openReplies") {
                defaultValue = false
            })
        ) { navBackStackEntry ->
            val uId = navBackStackEntry.arguments?.getString("postid")
            val refresh = navBackStackEntry.arguments?.getBoolean("refresh")
            val openReplies = navBackStackEntry.arguments?.getBoolean("openReplies")
            Logger.d("refresh") { refresh!!.toString() }
            Logger.d("openReplies") { openReplies!!.toString() }
            uId?.let { id ->
                SinglePostComposable(navController, postId = id, refresh == true, openReplies == true)
            }
        }

        composable(Destinations.Collection.route) { navBackStackEntry ->
            val uId = navBackStackEntry.arguments?.getString("collectionid")
            uId?.let { id ->
                CollectionComposable(navController, collectionId = id)
            }
        }

        composable(Destinations.Search.route) {
            ExploreComposable(navController)
        }

        composable(Destinations.Conversation.route) {
            ConversationsComposable(navController = navController)
        }

        composable(Destinations.Chat.route) { navBackStackEntry ->
            val uId = navBackStackEntry.arguments?.getString("userid")
            uId?.let { id ->
                ChatComposable(navController = navController, accountId = id)
            }
        }

        composable(Destinations.Mention.route) { navBackStackEntry ->
            val mentionId = navBackStackEntry.arguments?.getString("mentionid")
            mentionId?.let { id ->
                MentionComposable(navController = navController, mentionId = id)
            }
        }
    }
}

@Composable
fun BottomBar(
    navController: NavHostController,
    avatar: String,
    openAccountSwitchBottomSheet: () -> Unit
) {
    val screens = listOf(
        Destinations.HomeScreen,
        Destinations.Search,
        Destinations.NotificationsScreen,
        Destinations.OwnProfile
    )
    val systemNavigationBarHeight =
        WindowInsets.navigationBars.asPaddingValues().calculateBottomPadding()

    NavigationBar(
        modifier = Modifier.height(60.dp + systemNavigationBarHeight)
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route
        screens.forEach { screen ->
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
                                if (screen.route == Destinations.OwnProfile.route) {
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
            NavigationBarItem(icon = {
                if (screen.route == Destinations.OwnProfile.route && avatar.isNotBlank()) {
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
                } else if (currentRoute == screen.route) {
                    Icon(
                        imageVector = screen.activeIcon!!,
                        modifier = Modifier.size(30.dp),
                        contentDescription = stringResource(screen.label)
                    )
                } else {
                    Icon(
                        imageVector = screen.icon!!,
                        modifier = Modifier.size(30.dp),
                        contentDescription = stringResource(screen.label)
                    )
                }
            },
                selected = currentRoute == screen.route,
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = MaterialTheme.colorScheme.inverseSurface,
                    indicatorColor = Color.Transparent
                ),
                interactionSource = interactionSource,
                onClick = {
                    if (!isLongPress) {
                        Navigate.navigateWithPopUp(screen.route, navController)
                    }
                })
        }
    }
}