package com.daniebeler.pfpixelix.di

import com.daniebeler.pfpixelix.ui.composables.LoginViewModel
import com.daniebeler.pfpixelix.ui.composables.ThemeViewModel
import com.daniebeler.pfpixelix.ui.composables.hashtagMentionText.TextWithClickableHashtagsAndMentionsViewModel
import com.daniebeler.pfpixelix.ui.composables.post.PostViewModel
import com.daniebeler.pfpixelix.ui.composables.post.reply.ReplyElementViewModel
import com.daniebeler.pfpixelix.ui.composables.textfield_mentions.TextFieldMentionsViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.global_timeline.GlobalTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.hashtag_timeline.HashtagTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.home_timeline.HomeTimelineViewModel
import com.daniebeler.pfpixelix.ui.composables.timelines.local_timeline.LocalTimelineViewModel
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate

@Component
abstract class KmpViewModelComponent(
    @Component val app: AppComponent
) {
    abstract val loginViewModel: LoginViewModel
    abstract val themeViewModel: ThemeViewModel
    abstract val homeTimelineViewModel: HomeTimelineViewModel
    abstract val globalTimelineViewModel: GlobalTimelineViewModel
    abstract val localTimelineViewModel: LocalTimelineViewModel
    abstract val hashtagTimelineViewModel: HashtagTimelineViewModel
    abstract val postViewModel: PostViewModel
    abstract val textWithClickableHashtagsAndMentionsViewModel: TextWithClickableHashtagsAndMentionsViewModel
    abstract val textFieldMentionsViewModel: TextFieldMentionsViewModel
    abstract val replyElementViewModel: ReplyElementViewModel
    companion object
}

@KmpComponentCreate
expect fun KmpViewModelComponent.Companion.create(app: AppComponent): KmpViewModelComponent