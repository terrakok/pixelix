package com.daniebeler.pfpixelix.ui.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.dialog
import androidx.navigation.toRoute
import com.daniebeler.pfpixelix.EdgeToEdgeDialog
import com.daniebeler.pfpixelix.ui.composables.HomeComposable
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
import com.daniebeler.pfpixelix.ui.composables.session.LoginComposable
import com.daniebeler.pfpixelix.ui.composables.settings.about_instance.AboutInstanceComposable
import com.daniebeler.pfpixelix.ui.composables.settings.about_pixelix.AboutPixelixComposable
import com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts.BlockedAccountsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts.BookmarkedPostsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.followed_hashtags.FollowedHashtagsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.icon_selection.IconSelectionComposable
import com.daniebeler.pfpixelix.ui.composables.settings.liked_posts.LikedPostsComposable
import com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts.MutedAccountsComposable
import com.daniebeler.pfpixelix.ui.composables.single_post.SinglePostComposable
import com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline.HashtagTimelineComposable
import com.daniebeler.pfpixelix.utils.KmpUri
import com.daniebeler.pfpixelix.utils.toKmpUri
import kotlinx.serialization.Serializable

sealed interface Destination {
    @Serializable data class Hashtag(val hashtag: String) : Destination
    @Serializable data class HashtagTimeline(val hashtag: String) : Destination
    @Serializable data class Post(
        val id: String,
        val refresh: Boolean = false,
        val openReplies: Boolean = false
    ) : Destination
    @Serializable data class EditPost(val id: String) : Destination
    @Serializable data class Collection(val id: String) : Destination
    @Serializable data class Followers(val userId: String, val isFollowers: Boolean) : Destination
    @Serializable data object Conversations : Destination
    @Serializable data class Chat(val id: String) : Destination
    @Serializable data class Mention(val id: String) : Destination
    @Serializable data object EditProfile : Destination
    @Serializable data object IconSelection : Destination
    @Serializable data object MutedAccounts : Destination
    @Serializable data object BlockedAccounts : Destination
    @Serializable data object LikedPosts : Destination
    @Serializable data object BookmarkedPosts : Destination
    @Serializable data object FollowedHashtags : Destination
    @Serializable data object AboutInstance : Destination
    @Serializable data object AboutPixelix : Destination
    @Serializable data class Profile(val userId: String) : Destination
    @Serializable data class ProfileByUsername(val userName: String) : Destination
    @Serializable data object FirstLogin : Destination
    @Serializable data object NewLogin : Destination
    @Serializable data class Search(val page: Int = 0) : Destination
    @Serializable data object OwnProfile : Destination
    @Serializable data object HomeTabFeeds : Destination
    @Serializable data object HomeTabSearch : Destination
    @Serializable data class HomeTabNewPost(val uris: List<String> = emptyList()) : Destination
    @Serializable data object HomeTabNotifications : Destination
    @Serializable data object HomeTabOwnProfile : Destination
}

internal fun NavGraphBuilder.navigationGraph(
    navController: NavHostController,
    openPreferencesDrawer: () -> Unit,
    exitApp: () -> Unit
) {

    //login dialogs
    dialog<Destination.FirstLogin> {
        EdgeToEdgeDialog(
            onDismissRequest = exitApp,
            properties = DialogProperties(
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            )
        ) {
            LoginComposable(navController = navController)
        }
    }
    dialog<Destination.NewLogin> {
        EdgeToEdgeDialog(
            onDismissRequest = { navController.popBackStack() },
            properties = DialogProperties(
                dismissOnClickOutside = false,
                usePlatformDefaultWidth = false,
            )
        ) {
            LoginComposable(true, navController)
        }
    }

    //home tabs (with no transition animations)
    composable<Destination.HomeTabFeeds>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        HomeComposable(navController, openPreferencesDrawer)
    }

    composable<Destination.HomeTabSearch>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        ExploreComposable(navController)
    }

    composable<Destination.HomeTabNewPost>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.HomeTabNewPost>()
        val imageUris: List<KmpUri>? = args.uris.map { it.toKmpUri() }
        NewPostComposable(navController, imageUris)
    }

    composable<Destination.HomeTabNotifications>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        NotificationsComposable(navController)
    }

    composable<Destination.HomeTabOwnProfile>(
        enterTransition = { EnterTransition.None },
        exitTransition = { ExitTransition.None }
    ) {
        OwnProfileComposable(navController, openPreferencesDrawer)
    }

    //other screens
    composable<Destination.HashtagTimeline> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.HashtagTimeline>()
        HashtagTimelineComposable(navController, args.hashtag)
    }

    composable<Destination.Profile> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Profile>()
        OtherProfileComposable(navController, userId = args.userId, byUsername = null)
    }

    composable<Destination.ProfileByUsername> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.ProfileByUsername>()
        OtherProfileComposable(navController, userId = "", byUsername = args.userName)
    }

    composable<Destination.Hashtag> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Hashtag>()
        HashtagTimelineComposable(navController, args.hashtag)
    }

    composable<Destination.EditProfile> {
        EditProfileComposable(navController)
    }

    composable<Destination.IconSelection> {
        IconSelectionComposable(navController)
    }

    composable<Destination.EditPost> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.EditPost>()
        EditPostComposable(args.id, navController)
    }

    composable<Destination.MutedAccounts> {
        MutedAccountsComposable(navController)
    }

    composable<Destination.BlockedAccounts> {
        BlockedAccountsComposable(navController)
    }

    composable<Destination.LikedPosts> {
        LikedPostsComposable(navController)
    }

    composable<Destination.BookmarkedPosts> {
        BookmarkedPostsComposable(navController)
    }

    composable<Destination.FollowedHashtags> {
        FollowedHashtagsComposable(navController)
    }

    composable<Destination.AboutInstance> {
        AboutInstanceComposable(navController)
    }

    composable<Destination.AboutPixelix> {
        AboutPixelixComposable(navController)
    }

    composable<Destination.OwnProfile> {
        OwnProfileComposable(navController, openPreferencesDrawer)
    }

    composable<Destination.Followers> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Followers>()
        FollowersMainComposable(
            navController,
            accountId = args.userId,
            isFollowers = args.isFollowers
        )
    }

    composable<Destination.Post> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Post>()
        SinglePostComposable(navController, postId = args.id, args.refresh, args.openReplies)
    }

    composable<Destination.Collection> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Collection>()
        CollectionComposable(navController, collectionId = args.id)
    }

    composable<Destination.Search> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Search>()
        ExploreComposable(navController, args.page)
    }

    composable<Destination.Conversations> {
        ConversationsComposable(navController = navController)
    }

    composable<Destination.Chat> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Chat>()
        ChatComposable(navController = navController, accountId = args.id)
    }

    composable<Destination.Mention> { navBackStackEntry ->
        val args = navBackStackEntry.toRoute<Destination.Mention>()
        MentionComposable(navController = navController, mentionId = args.id)
    }
}