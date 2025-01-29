package com.daniebeler.pfpixelix.domain.model


//@Immutable todo
data class Post(
    val id: String,
    val mediaAttachments: List<MediaAttachment>,
    val account: Account,
    val tags: List<Tag>,
    val favouritesCount: Int,
    val content: String,
    val replyCount: Int,
    val createdAt: String,
    val url: String,
    val sensitive: Boolean,
    val spoilerText: String,
    var favourited: Boolean,
    var reblogged: Boolean,
    val bookmarked: Boolean,
    val mentions: List<Account>,
    val place: Place?,
    val likedBy: LikedBy?,
    val visibility: String,
    val inReplyToId: String?,
    val rebloggedBy: Account? = null,
    val reblogId: String? = null
)