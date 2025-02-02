package com.daniebeler.pfpixelix.di

import com.daniebeler.pfpixelix.ui.composables.LoginViewModel
import com.daniebeler.pfpixelix.ui.composables.ThemeViewModel
import com.daniebeler.pfpixelix.ui.composables.collection.CollectionViewModel
import com.daniebeler.pfpixelix.ui.composables.custom_account.CustomAccountViewModel
import com.daniebeler.pfpixelix.ui.composables.direct_messages.chat.ChatViewModel
import com.daniebeler.pfpixelix.ui.composables.direct_messages.conversations.ConversationsViewModel
import com.daniebeler.pfpixelix.ui.composables.edit_post.EditPostViewModel
import com.daniebeler.pfpixelix.ui.composables.edit_profile.EditProfileViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.ExploreViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts.TrendingAccountElementViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_accounts.TrendingAccountsViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.TrendingHashtagElementViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_hashtags.TrendingHashtagsViewModel
import com.daniebeler.pfpixelix.ui.composables.explore.trending.trending_posts.TrendingPostsViewModel
import com.daniebeler.pfpixelix.ui.composables.followers.FollowersViewModel
import com.daniebeler.pfpixelix.ui.composables.hashtagMentionText.TextWithClickableHashtagsAndMentionsViewModel
import com.daniebeler.pfpixelix.ui.composables.mention.MentionViewModel
import com.daniebeler.pfpixelix.ui.composables.newpost.NewPostViewModel
import com.daniebeler.pfpixelix.ui.composables.notifications.CustomNotificationViewModel
import com.daniebeler.pfpixelix.ui.composables.notifications.NotificationsViewModel
import com.daniebeler.pfpixelix.ui.composables.post.PostViewModel
import com.daniebeler.pfpixelix.ui.composables.post.reply.ReplyElementViewModel
import com.daniebeler.pfpixelix.ui.composables.profile.other_profile.OtherProfileViewModel
import com.daniebeler.pfpixelix.ui.composables.profile.own_profile.AccountSwitchViewModel
import com.daniebeler.pfpixelix.ui.composables.profile.own_profile.OwnProfileViewModel
import com.daniebeler.pfpixelix.ui.composables.profile.server_stats.ServerStatsViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.about_instance.AboutInstanceViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.about_pixelix.AboutPixelixViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.blocked_accounts.BlockedAccountsViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.bookmarked_posts.BookmarkedPostsViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.followed_hashtags.FollowedHashtagsViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.icon_selection.IconSelectionViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.liked_posts.LikedPostsViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.muted_accounts.MutedAccountsViewModel
import com.daniebeler.pfpixelix.ui.composables.settings.preferences.PreferencesViewModel
import com.daniebeler.pfpixelix.ui.composables.single_post.SinglePostViewModel
import com.daniebeler.pfpixelix.ui.composables.textfield_location.TextFieldLocationsViewModel
import com.daniebeler.pfpixelix.ui.composables.textfield_mentions.TextFieldMentionsViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.global_timeline.GlobalTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline.HashtagTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline.HomeTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline.LocalTimelineViewModel
import me.tatarka.inject.annotations.Component

@Component
abstract class ViewModelComponent(
    @Component val app: AndroidAppComponent
) {
}