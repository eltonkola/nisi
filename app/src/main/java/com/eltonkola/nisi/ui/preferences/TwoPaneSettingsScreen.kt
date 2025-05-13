package com.eltonkola.nisi.ui.preferences

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.tv.foundation.ExperimentalTvFoundationApi
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.ListItem
import androidx.tv.material3.ListItemDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.eltonkola.nisi.ui.preferences.wallpaper.WallpaperSettingsSection
import gridIcon
import homeIcon
import imageIcon
import infoIcon
import lockIcon
import weatherIcon


enum class SettingsSection(val title: String, val icon: ImageVector) {
    WEATHER("Weather", weatherIcon),
    ACCESSIBILITY("Accessibility", homeIcon),
    PIN("PIN Lock", lockIcon),
    APPS("Apps", gridIcon),
    WALLPAPER("Wallpaper", imageIcon),
    ABOUT("About", infoIcon),
}

@OptIn(ExperimentalTvMaterial3Api::class, ExperimentalTvFoundationApi::class)
@Composable
fun TwoPaneSettingsScreen() {
    var selectedSection by remember { mutableStateOf(SettingsSection.WEATHER) } // Start with Weather

    Row(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface) // Use theme background
    ) {
        NavigationPane(
            sections = SettingsSection.entries.toList(), // Get all enum values
            selectedSection = selectedSection,
            onSectionSelected = { section -> selectedSection = section },
            modifier = Modifier
                .weight(0.3f) // Adjust weight as needed (e.g., 30%)
                .fillMaxHeight()
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)) // Slightly different background
        )

        ContentPane(
            selectedSection = selectedSection,
            modifier = Modifier
                .weight(0.7f) // Adjust weight as needed (e.g., 70%)
                .fillMaxHeight()
                .padding(start = 24.dp, end = 48.dp, top = 32.dp, bottom = 32.dp) // Add padding
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun NavigationPane(
    sections: List<SettingsSection>,
    selectedSection: SettingsSection,
    onSectionSelected: (SettingsSection) -> Unit,
    modifier: Modifier = Modifier
) {
    val listState = rememberLazyListState()

    LaunchedEffect(selectedSection) {
        // Ensure the selected item is visible when the screen loads or selection changes
        val index = sections.indexOf(selectedSection)
        if (index != -1) {
            // Consider scrolling logic if needed, e.g., listState.animateScrollToItem(index)
        }
    }

    LazyColumn(
        modifier = modifier.padding(vertical = 32.dp), // Padding top/bottom
        state = listState,
        verticalArrangement = Arrangement.spacedBy(8.dp), // Space between items
        contentPadding = PaddingValues(horizontal = 16.dp) // Padding left/right
    ) {
        itemsIndexed(sections, key = { _, section -> section.name }) { index, section ->
            val isSelected = section == selectedSection
            ListItem( // Use ListItem for standard TV list items
                selected = isSelected,
                onClick = { onSectionSelected(section) },
                // Optional: Use onFocus if you want the right pane to update immediately on focus shift
                // modifier = Modifier.onFocusChanged { if(it.isFocused) onSectionSelected(section) }
                headlineContent = { Text(section.title) },
                leadingContent = { // Add icon
                    Icon(
                        imageVector = section.icon,
                        contentDescription = null,
                        modifier = Modifier.size(ListItemDefaults.IconSize)
                    )
                },
                colors = ListItemDefaults.colors( // Use default colors for selection indication
                    containerColor = Color.Transparent, // Keep transparent background
                    // Customize focused/selected colors if needed
                    // focusedContainerColor = TvMaterialTheme.colorScheme.onSurface.copy(alpha = 0.1f),
                    // selectedContainerColor = TvMaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                )
            )
        }
    }
}

@Composable
private fun ContentPane(
    selectedSection: SettingsSection,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier) {
        when (selectedSection) {
            SettingsSection.WEATHER -> WeatherSettingsSection()
            SettingsSection.APPS -> AppsSettingsSection()
            SettingsSection.WALLPAPER -> WallpaperSettingsSection()
            SettingsSection.ABOUT -> AboutSettingsSection()
            SettingsSection.ACCESSIBILITY -> AccessibilitySettingsSection()
            SettingsSection.PIN -> PinSettingsSection()
        }
    }
}


