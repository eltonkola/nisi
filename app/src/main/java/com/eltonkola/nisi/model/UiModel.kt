package com.eltonkola.nisi.model

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.vector.ImageVector

data class App(
    val name: String,
    val packageName: String,
    val icon: ImageBitmap? = null // Use ImageBitmap from Compose Graphics
)

data class BottomTab(
    val name: String,
    val icon: ImageVector,
    val action: () -> Unit
)
