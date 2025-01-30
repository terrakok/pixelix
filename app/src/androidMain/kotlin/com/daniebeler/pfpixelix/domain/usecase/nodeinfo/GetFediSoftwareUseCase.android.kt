package com.daniebeler.pfpixelix.domain.usecase.nodeinfo

import com.daniebeler.pfpixelix.R
import pixelix.app.generated.resources.Res
import pixelix.app.generated.resources.*

internal actual fun getSlugIcon(slug: String): Int = when (slug) {
    "pixelfed" -> R.drawable.pixelfed_logo
    "mastodon" -> R.drawable.mastodon_logo
    "peertube" -> R.drawable.peertube_logo
    "lemmy" -> R.drawable.lemmy_logo
    "misskey" -> R.drawable.misskey_logo
    else -> R.drawable.fediverse_logo
}