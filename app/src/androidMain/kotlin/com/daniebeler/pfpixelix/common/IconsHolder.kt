package com.daniebeler.pfpixelix.common

import androidx.annotation.DrawableRes
import com.daniebeler.pfpixelix.R

object IconsHolder {
    val list = listOf(
        IconAndName("com.daniebeler.pfpixelix.Icon03", R.mipmap.ic_launcher_03),
        IconAndName("com.daniebeler.pfpixelix.Icon01", R.mipmap.ic_launcher_01),
        IconAndName("com.daniebeler.pfpixelix.Icon05", R.mipmap.ic_launcher_05),
        IconAndName("com.daniebeler.pfpixelix.Icon06", R.mipmap.ic_launcher_06),
        IconAndName("com.daniebeler.pfpixelix.Icon07", R.mipmap.ic_launcher_07),
        IconAndName("com.daniebeler.pfpixelix.Icon08", R.mipmap.ic_launcher_08),
        IconAndName("com.daniebeler.pfpixelix.Icon09", R.mipmap.ic_launcher_09),
        IconAndName("com.daniebeler.pfpixelix.Icon04", R.mipmap.ic_launcher)
    )
}

data class IconAndName(
    val name: String, @DrawableRes val iconResourceId: Int
)