package com.eltonkola.nisi.ui.preferences

import android.content.Intent
import android.provider.Settings
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.TextButton

import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import androidx.tv.material3.* // Import TV Material 3
import com.eltonkola.nisi.model.AppSettingItem
import iconChevronDown
import iconChevronUp
import iconEye
import iconEyeOff
import iconHeart
import iconHeartOff
import iconLock
import iconUnlock

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppsSettingsSection(
    viewModel: AppsSettingsViewModel = hiltViewModel()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current // Get context if needed for anything else

    // Extract the specific app for the dialog for clarity
    val appForDialog = uiState.appToShowActionsFor

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 48.dp, vertical = 24.dp),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        Text("Apps Settings", style = MaterialTheme.typography.headlineMedium)
        Text("Select an app to manage its visibility, order, or lock status.") // Updated text

        Spacer(modifier = Modifier.height(16.dp))

        // --- Loading/Error/Empty States (remain the same) ---
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("Error: ${uiState.error}", color = MaterialTheme.colorScheme.error)
            }
        } else if (uiState.appSettings.isEmpty()) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Text("No applications found or managed.")
            }
        } else {
            // Pass the onAppClick lambda down
            AppList(
                apps = uiState.appSettings,
                onAppClick = { app ->
                    viewModel.showActionsForApp(app) // Trigger showing the dialog
                }
            )
        }
    } // End Column

    // --- Conditionally display the Dialog ---
    if (appForDialog != null) {
        val isFirst = uiState.appSettings.firstOrNull()?.packageName == appForDialog.packageName
        val isLast = uiState.appSettings.lastOrNull()?.packageName == appForDialog.packageName

        AppActionsDialog(
            app = appForDialog,
            isFirst = isFirst,
            isLast = isLast,
            onDismissRequest = { viewModel.dismissActionsDialog() },
            onToggleVisibility = {
                viewModel.toggleVisibility(appForDialog.packageName)
                viewModel.dismissActionsDialog() // Dismiss after action
            },
            onToggleLock = {
                viewModel.toggleLock(appForDialog.packageName)
                viewModel.dismissActionsDialog() // Dismiss after action
            },
            onMoveUp = {
                viewModel.moveApp(appForDialog.packageName, MoveDirection.UP)
                viewModel.dismissActionsDialog() // Dismiss after action
            },
            onMoveDown = {
                viewModel.moveApp(appForDialog.packageName, MoveDirection.DOWN)
                viewModel.dismissActionsDialog() // Dismiss after action
            },
            onToggleFavorite = {
                viewModel.toggleFavorite(appForDialog.packageName)
                viewModel.dismissActionsDialog() // Dismiss after action
            }
        )
    }
}


@OptIn(ExperimentalTvMaterial3Api::class) // Still might need this for theme/colors
@Composable
fun AppActionsDialog(
    app: AppSettingItem,
    isFirst: Boolean,
    isLast: Boolean,
    onDismissRequest: () -> Unit,
    onToggleVisibility: () -> Unit,
    onToggleLock: () -> Unit,
    onMoveUp: () -> Unit,
    onMoveDown: () -> Unit,
    onToggleFavorite: () -> Unit
) {
    // Use standard AlertDialog - it adapts reasonably well for simple TV dialogs
    AlertDialog(
        onDismissRequest = onDismissRequest,
        // Title showing the app name
        title = { Text(text = app.name) },
        // Content is the list of actions
        text = {
            // Use a Column for the actions. TvLazyColumn is overkill for few items.
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                // Show/Hide Action
                DialogActionItem(
                    text = if (app.isFavorite) "Remove from Favorites" else "Add to Favorites",
                    icon = if (app.isFavorite) iconHeartOff else iconHeart, // Use Star/StarBorder
                    onClick = onToggleFavorite // Use the new callback
                )

                DialogActionItem(
                    text = if (app.isVisible) "Hide from list" else "Show in list",
                    icon = if (app.isVisible) iconEyeOff else iconEye,
                    onClick = onToggleVisibility
                )

                // Move Up Action
                DialogActionItem(
                    text = "Move Up",
                    icon = iconChevronUp,
                    enabled = !isFirst, // Disable if it's the first item
                    onClick = onMoveUp
                )

                // Move Down Action
                DialogActionItem(
                    text = "Move Down",
                    icon = iconChevronDown,
                    enabled = !isLast, // Disable if it's the last item
                    onClick = onMoveDown
                )

                // Lock/Unlock Action
                DialogActionItem(
                    text = if (app.isLocked) "Unlock Reordering/Hiding" else "Lock Reordering/Hiding",
                    icon = if (app.isLocked) iconUnlock else iconLock,
                    // Disable other actions if locked? Or just show lock status?
                    // Here, we just allow toggling the lock itself.
                    onClick = onToggleLock
                )
            }
        },
        // Confirm button isn't really needed if actions are immediate
        // You could have a "Close" button, but the dismiss callback handles that
        confirmButton = {
            TextButton(onClick = onDismissRequest) {
                Text("Close")
            }
        }
        // modifier = Modifier.width(IntrinsicSize.Max) // Adjust width if needed
    )
}

// Helper composable for items within the dialog for consistency
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun DialogActionItem(
    text: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    enabled: Boolean = true,
    onClick: () -> Unit
) {
    // Using a Surface or Card makes it clearly focusable on TV within the dialog
    Card( // Or Surface
        onClick = onClick,
        //enabled = enabled,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null, // Text describes the action
                tint = if (enabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.5f)
            )
            Text(
                text = text,
                style = MaterialTheme.typography.bodyLarge, // Use appropriate Dialog text style
                color = if (enabled) LocalContentColor.current else LocalContentColor.current.copy(alpha = 0.5f)
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppList(
    apps: List<AppSettingItem>,
    onAppClick: (AppSettingItem) -> Unit
) {
    // TvLazyColumn is optimized for TV navigation
    LazyColumn(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp),
        contentPadding = PaddingValues(bottom = 100.dp) // Ensure space at bottom
    ) {
        itemsIndexed (
            items = apps,
            key = { _, item -> item.packageName } // Important for performance and state preservation
        ) { index, app ->
            AppSettingRow(
                app = app,
                onClick = { onAppClick(app) }
            )
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppSettingRow(
    app: AppSettingItem,
    onClick: () -> Unit
) {


    // Use Card or Surface for better visual grouping and focus indication
    Card( // Or Surface
        onClick = {  onClick() }, // Card itself can be clickable/focusable
        modifier = Modifier.fillMaxWidth()
        // Optional: Add visual indication if hidden
        // .alpha(if (app.isVisible) 1f else 0.6f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // App Info (Icon, Name, Package) - Takes up available space
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f).padding(end = 16.dp) // Ensure space before buttons
            ) {
                // App Icon
                Box(
                    modifier = Modifier.size(40.dp), // Standard icon size
                    contentAlignment = Alignment.Center
                ) {
                    app.icon?.let {
                        Image(
                            bitmap = it,
                            contentDescription = "${app.name} icon",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Fit
                        )
                    } ?: Spacer(Modifier.size(40.dp)) // Placeholder if no icon
                }

                Spacer(modifier = Modifier.width(16.dp))

                // App Name and Package Name
                Column {
                    Text(
                        text = app.name,
                        style = MaterialTheme.typography.bodyLarge,
                        maxLines = 1
                        // color = if (app.isVisible) LocalContentColor.current else Color.Gray // Dim if hidden
                    )
                    Text(
                        text = app.packageName,
                        style = MaterialTheme.typography.bodySmall,
                        maxLines = 1,
                        color = LocalContentColor.current.copy(alpha = 0.7f)
                    )
                }
            } // End App Info Row

            // Action Buttons - Fixed space at the end
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp) // Spacing between buttons
            ) {
                // Show/Hide Button

                    Icon(
                        imageVector = if (app.isVisible) iconEye else iconEyeOff,
                        contentDescription = if (app.isVisible) "Hide App" else "Show App",
                        tint = if (app.isVisible) LocalContentColor.current else Color.Gray // Visual cue
                    )


                    Icon(
                        imageVector = if (app.isFavorite) iconHeart else iconHeartOff,
                        contentDescription = "Favorite",
                        modifier = Modifier.size(18.dp), // Consistent size
                        tint = MaterialTheme.colorScheme.primary // Make it stand out
                    )


                    Icon(
                        imageVector = if (app.isLocked) iconLock else iconUnlock,
                        contentDescription = if (app.isLocked) "Unlock App" else "Lock App",
                        tint = if (app.isLocked) MaterialTheme.colorScheme.primary else LocalContentColor.current // Highlight if locked
                    )
                }

        } // End Main Row
    } // End Card
}