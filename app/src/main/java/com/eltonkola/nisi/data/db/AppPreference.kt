package com.eltonkola.nisi.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_preferences")
data class AppPreference(
    @PrimaryKey val packageName: String,
    val isVisible: Boolean = true,
    val orderIndex: Int = Int.MAX_VALUE,
    val isLocked: Boolean = false,
    val isFavorite: Boolean = false,
//    val isInstalled: Boolean = true
)