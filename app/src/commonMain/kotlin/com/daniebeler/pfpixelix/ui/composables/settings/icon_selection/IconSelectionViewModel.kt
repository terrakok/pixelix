package com.daniebeler.pfpixelix.ui.composables.settings.icon_selection

import androidx.compose.ui.graphics.ImageBitmap
import androidx.lifecycle.ViewModel
import com.daniebeler.pfpixelix.domain.usecase.DisableAllCustomAppIconsUseCase
import com.daniebeler.pfpixelix.domain.usecase.EnableCustomAppIconUseCase
import com.daniebeler.pfpixelix.utils.KmpContext
import me.tatarka.inject.annotations.Inject

class IconSelectionViewModel @Inject constructor(
    private val disableAllCustomAppIconsUseCase: DisableAllCustomAppIconsUseCase,
    private val enableCustomAppIconUseCase: EnableCustomAppIconUseCase
) : ViewModel() {

    var icons = mutableListOf<IconWithName>()

    fun fillList(context: KmpContext) {
        //todo
//        if (icons.isEmpty()) {
//            val mainDrawable = ResourcesCompat.getDrawableForDensity(
//                KmpContext.resources,
//                R.mipmap.ic_launcher_02,
//                DisplayMetrics.DENSITY_XXXHIGH,
//                KmpContext.theme
//            )
//
//            val mainBitmap = mainDrawable!!.toBitmap(
//                mainDrawable.minimumWidth, mainDrawable.minimumHeight
//            ).asImageBitmap()
//            icons.add(IconWithName("com.daniebeler.pfpixelix.MainActivity", mainBitmap, false))
//
//            IconsHolder.list.forEach {
//                val drawable = ResourcesCompat.getDrawableForDensity(
//                    KmpContext.resources,
//                    it.iconResourceId,
//                    DisplayMetrics.DENSITY_XXXHIGH,
//                    KmpContext.theme
//                )
//
//                val bitmap = drawable!!.toBitmap(
//                    drawable.minimumWidth, drawable.minimumHeight
//                ).asImageBitmap()
//
//                icons.add(IconWithName(it.name, bitmap, false))
//            }
//
//            setEnabledValues(context)
//        }
    }

    private fun setEnabledValues(context: KmpContext) {
        //todo
//        val newList = mutableListOf<IconWithName>()
//        val packageManager = KmpContext.packageManager
//
//        var foundItem = false
//
//        icons.forEach {
//            if (it.name == "com.daniebeler.pfpixelix.MainActivity") {
//                newList.add(IconWithName(it.name, it.icon, false))
//            }
//            else {
//                val enabled = packageManager.getComponentEnabledSetting(
//                    ComponentName(
//                        KmpContext, it.name
//                    )
//                )
//
//                foundItem = foundItem || enabled == 1
//
//                newList.add(IconWithName(it.name, it.icon, enabled == 1))
//            }
//        }
//
//        if (!foundItem) {
//            if (newList.size > 0) {
//                newList[0] = IconWithName(icons[0].name, icons[0].icon, true)
//            }
//        }
//
//        icons = newList
    }

    fun changeIcon(context: KmpContext, name: String) {
        disableAllCustomAppIconsUseCase(context)
        enableCustomAppIconUseCase(context, name)
        setEnabledValues(context)
    }
}

data class IconWithName(
    val name: String, val icon: ImageBitmap, val enabled: Boolean = false
)