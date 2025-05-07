package com.eltonkola.nisi.ui.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector
import com.eltonkola.nisi.data.model.AppSettingItem
import com.eltonkola.nisi.ui.icons.iconPlay
import iconChevronLeft
import iconChevronRight
import iconDelete
import iconEyeOff
import iconHeart
import iconHeartOff
import infoIcon

interface AppItemActions {
    fun launch(item: AppSettingItem)
    fun info(item: AppSettingItem)
    fun uninstall(item: AppSettingItem)
    fun favorites(item: AppSettingItem, favorite: Boolean)
    fun showHide(item: AppSettingItem, show: Boolean)
    fun lockUnlock(item: AppSettingItem, lock: Boolean)
    fun moveLeft(item: AppSettingItem)
    fun moveRight(item: AppSettingItem)
}

data class AppMenuItem(
    val title: String,
    val icon: ImageVector,
    val onClick: () -> Unit,
    val protected: Boolean = true,
)


fun AppSettingItem.getMenuActions(actions: AppItemActions, allApps: Boolean = false, unlocked: Boolean = false): List<AppMenuItem> {
    //unlocked app can do everything, locked app can only do a couple of things
    val open = AppMenuItem(
        title = "Open ${this.name}",
        icon = iconPlay,
        onClick = { actions.launch(this) }
    )
    val info = AppMenuItem(
        title = "Information",
        icon = infoIcon,
        onClick = { actions.info(this) }
    )

    val moveLeft = AppMenuItem(
        title = "Move Left",
        icon = iconChevronLeft,
        onClick = { actions.moveLeft(this) }
    )

    val moveRight = AppMenuItem(
        title = "Move Right",
        icon = iconChevronRight,
        onClick = { actions.moveRight(this) }
    )

    val uninstall = AppMenuItem(
        title = "Uninstall ${this.name}",
        icon = iconDelete,
        onClick = { actions.uninstall(this) }
    )

    val addFavorite = AppMenuItem(
        title = "Add to Favorites",
        icon = iconHeart,
        onClick = { actions.favorites(this, true) }
    )

    val removeFavorite = AppMenuItem(
        title = "Remove from Favorites",
        icon = iconHeartOff,
        onClick = { actions.favorites(this, false) }
    )

    val hide = AppMenuItem(
        title = "Hide",
        icon = iconEyeOff,
        onClick = { actions.showHide(this, false) }
    )

    return if(unlocked){
        listOf(
            open,
            info,
        )
    }else{
        if(allApps){
            listOf(
                open,
                info,
                moveLeft,
                moveRight,
                if(this.isFavorite) removeFavorite else addFavorite,
                hide,
                uninstall
            )
        }else{
            listOf(
                open,
                info,
                moveLeft,
                moveRight,
                if(this.isFavorite) removeFavorite else addFavorite,
            )
        }
    }
}

