package com.eltonkola.nisi.ui.launcher

import android.R.attr.onClick
import android.util.Log
import android.view.MenuItem
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Glow
import androidx.tv.material3.Icon
import androidx.tv.material3.Text
import com.eltonkola.nisi.data.model.AppSettingItem
import com.eltonkola.nisi.ui.components.rememberPulsatingBorderStroke
import com.eltonkola.nisi.ui.model.AppMenuItem

import kotlinx.coroutines.delay


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppItemUi(
    app: AppSettingItem,
    menuItems: List<AppMenuItem>,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }
    var currentLongPressedApp by remember { mutableStateOf<AppSettingItem?>(null) }
    var menuPosition by remember { mutableStateOf(DpOffset.Zero) }
    val density = LocalDensity.current
    val menuFocusRequester = remember { FocusRequester() }
    var menuItemsInteractable by remember { mutableStateOf(false) }



//    val interactionSource = remember { MutableInteractionSource() }
//    val isFocused by interactionSource.collectIsFocusedAsState()
//    val customSpectralBorder = rememberSpectralAnimatedBorderStroke(
//        isFocused = isFocused,
//        baseColors = listOf(Color.Magenta, Color.Yellow, Color.Cyan),
//        borderWidthFocused = 1.dp,
//        borderWidthNormal = 0.5.dp,
//        animationDurationMillis = 400
//    )


    Card(
//        interactionSource = interactionSource,
        onClick = menuItems.first().onClick,
        onLongClick = {
            currentLongPressedApp = app
            showMenu = true
            menuItemsInteractable = false
        },
        modifier = modifier
            .width(180.dp)
            .aspectRatio(CardDefaults.HorizontalImageAspectRatio)
            .onGloballyPositioned { coordinates ->

                with(density) {
                    menuPosition = DpOffset(
                        x = coordinates.positionInWindow().x.toDp() + (coordinates.size.width / 2).toDp(),
                        y = coordinates.positionInWindow().y.toDp() + (coordinates.size.height / 2).toDp()
                    )
                }

            },
//        border =
//            CardDefaults.border(
//                focusedBorder =
//                    Border(
//                        border = BorderStroke(width = 1.dp, color = Color.White),
//                        shape = RoundedCornerShape(8),
//                    ),
//            ),
        border =
            CardDefaults.border(
                focusedBorder =
                    Border(
                        border =rememberPulsatingBorderStroke(
                            width = 2.dp,
                            fromColor = Color.White,
                            toColor = Color.LightGray,
                            durationMillis = 400
                        ),
                        shape = RoundedCornerShape(8),
                    ),
            ),
//        border =
//            CardDefaults.border(
//                focusedBorder =
//                    Border(
//                        customSpectralBorder,
//                        shape = RoundedCornerShape(8),
//                    ),
//            ),



        colors =
            CardDefaults.colors(
//                containerColor = Color.Gray,
//                focusedContainerColor = Color.DarkGray
            ),
        scale =
            CardDefaults.scale(
                focusedScale = 1.1f,
            ),
        glow = CardDefaults.glow(
            focusedGlow = Glow(elevationColor = Color.Black, elevation = 6.dp),
            pressedGlow = Glow(elevationColor = Color.Yellow, elevation = 8.dp),
            glow = Glow(elevationColor = Color.Gray, elevation = 4.dp),
        ),
    ) {

        if (app.icon != null) {
            Image(
                bitmap = app.icon,
                contentDescription = app.name,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.FillWidth
            )
        } else {
            Icon(
                imageVector = Icons.Default.Info,
                contentDescription = app.name,
                tint = Color.White,
                modifier = Modifier.size(40.dp)
            )
        }
    }

    if (showMenu && currentLongPressedApp != null) {
        var isCompositionComplete by remember { mutableStateOf(false) }

        BackHandler {
            showMenu = false
        }

        DropdownMenu(
            modifier = Modifier
                .onGloballyPositioned {
                    isCompositionComplete = true
                }
                .focusRequester(menuFocusRequester),
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            offset = menuPosition
        ) {

            menuItems.forEach { item ->
                MenuItem(
                    enabled = menuItemsInteractable,
                    text = item.title,
                    onClick = {
                        item.onClick()
                        showMenu = false
                    },
                    leadingIcon = {
                        Icon(item.icon, contentDescription = item.title)
                    }
                )
            }


        }

        LaunchedEffect(isCompositionComplete) {
            if (isCompositionComplete) {
                try {
                    // Slight delay to ensure UI is ready
                    delay(100)
                    menuFocusRequester.requestFocus()

                    // Wait additional time before enabling menu items
                    delay(400)
                    menuItemsInteractable = true
                } catch (e: Exception) {
                    // Handle any focus request exceptions
                    Log.e("AppGrid", "Focus request failed", e)
                }
            }
        }


    }

}


@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalComposeUiApi::class)
@Composable
private fun MenuItem(
    text: String,
    enabled: Boolean,
    onClick: () -> Unit,
    leadingIcon: @Composable () -> Unit,
) {

    DropdownMenuItem(
        text = { Text(text) },
        leadingIcon = leadingIcon,
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.fillMaxWidth() .focusProperties {
            if (!enabled) {
                canFocus = false
                onEnter = { FocusRequester.Cancel }
            }
        }
    )



}



