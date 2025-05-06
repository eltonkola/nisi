package com.eltonkola.nisi.ui.launcher

import android.R.attr.text
import android.util.Log
import android.view.MenuItem
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusProperties
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.layout.positionInWindow
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.unit.DpOffset
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ClickableSurfaceDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.NavigationDrawerItemDefaults
import androidx.tv.material3.Surface
import androidx.tv.material3.Text
import com.eltonkola.nisi.data.model.App
import com.eltonkola.nisi.data.model.AppSettingItem
import kotlinx.coroutines.delay


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppItemUi(
    app: AppSettingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMenu by remember { mutableStateOf(false) }
    var currentLongPressedApp by remember { mutableStateOf<AppSettingItem?>(null) }
    var menuPosition by remember { mutableStateOf(DpOffset.Zero) }
    val density = LocalDensity.current
    val menuFocusRequester = remember { FocusRequester() }
    var menuItemsInteractable by remember { mutableStateOf(false) }

        Card(
            onClick = onClick,
            onLongClick =  {
                currentLongPressedApp = app
                showMenu = true
                // Initially disable menu item interaction
                menuItemsInteractable = false
            }
                ,
            modifier = modifier.width(180.dp)
                .aspectRatio(CardDefaults.HorizontalImageAspectRatio)
                .onGloballyPositioned { coordinates ->


                    with(density) {
                        // Get the center position of the item
                        menuPosition = DpOffset(
                            x = coordinates.positionInWindow().x.toDp() + (coordinates.size.width / 2).toDp(),
                            y = coordinates.positionInWindow().y.toDp() + (coordinates.size.height / 2).toDp()
                        )
                    }

                }
                ,
            border =
                CardDefaults.border(
                    focusedBorder =
                        Border(
                            border = BorderStroke(width = 2.dp, color = Color.White),
                            shape = RoundedCornerShape(8),
                        ),
                ),
            colors =
                CardDefaults.colors(
                    containerColor = Color.Gray,
                    focusedContainerColor = Color.DarkGray
                ),
            scale =
                CardDefaults.scale(
                    focusedScale = 1.1f,
                )
        ) {

            if (app.icon != null) {
                Image(
                    bitmap = app.icon,
                    contentDescription = app.name,
                    modifier = Modifier.fillMaxSize(),
                    contentScale = ContentScale.Fit
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
                .focusRequester(menuFocusRequester)
            ,
            expanded = showMenu,
            onDismissRequest = { showMenu = false },
            offset = menuPosition
        ) {

                    MenuItem(
                        enabled = menuItemsInteractable,
                        text = "Open ${app.name}",
                        onClick = {


                            onClick()
                            showMenu = false

                        },
                        leadingIcon = { // Optional icon
                            Icon(Icons.Default.Info, contentDescription = "Open")
                        }
                    )
                    MenuItem(
                        enabled = menuItemsInteractable,
                        text = "Hide ${app.name}",
                        onClick = {
                            //TODO Hide app

                            showMenu = false

                        },
                        leadingIcon = {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                    )
                    MenuItem(
                        enabled = menuItemsInteractable,
                        text = "App Details",
                        onClick = {
                            //TODO Open app details

                            showMenu = false

                        },
                        leadingIcon = {
                            Icon(Icons.Default.Settings, contentDescription = "Settings")
                        }
                    )
                    MenuItem(
                        enabled = menuItemsInteractable,
                        text = "Uninstall ${app.name}",
                        onClick = {
                            //TODO Uninstall app
                            showMenu = false
                        },
                        leadingIcon = {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                    )
//                }
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
    Surface(
        onClick = onClick,
        enabled = enabled,
        colors = ClickableSurfaceDefaults.colors(
            containerColor = MaterialTheme.colorScheme.secondaryContainer,
            contentColor = MaterialTheme.colorScheme.onSecondaryContainer,
            disabledContainerColor = Color.Transparent,
            disabledContentColor = MaterialTheme.colorScheme.onSecondaryContainer
        ),

        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
            // Use focusProperties to control focus behavior
            .focusProperties {
                if (!enabled) {
                    canFocus = false
                    onEnter = { FocusRequester.Cancel }
                }
            }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }
}



