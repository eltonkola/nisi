package com.eltonkola.nisi.ui.landing


import android.R.attr.end
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextFieldDefaults.contentPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.Button
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.eltonkola.nisi.R
import com.eltonkola.nisi.data.model.AppSettingItem
import iconHeart
import iconHeartOff
import iconSettings

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppsSelectionScreen(
    viewModel: AppsLandingViewModel = hiltViewModel(),
    onNext:() -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val firstAppFocusRequester = remember { FocusRequester() }
    val firtstAppFocused = remember { mutableStateOf(false) }

        Row(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background)
                .padding(start = 48.dp, top = 48.dp, bottom = 48.dp, end = 24.dp) // More padding on start for TV
        ) {
            // Left Pane: Logo and Text
            Column(
                modifier = Modifier
                    .weight(0.35f) // Adjust weight as needed
                    .fillMaxHeight()
                    .padding(end = 32.dp),
                horizontalAlignment = Alignment.Start, // Align to start for left pane
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    imageVector = iconSettings,
                    contentDescription = "NISI Launcher Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 24.dp)
                )
                Text(
                    text = stringResource(R.string.apps_screen_title),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    text = stringResource(R.string.apps_screen_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            // Right Pane: Apps List
            Column (
                modifier = Modifier
                    .weight(0.65f)
                    .fillMaxHeight(),
            ) {
                when {
                    uiState.isLoading -> {
                        CircularProgressIndicator(modifier = Modifier.size(48.dp))
                    }

                    uiState.error != null -> {
                        Text(
                            text = "Error: ${uiState.error}",
                            color = MaterialTheme.colorScheme.error,
                            textAlign = TextAlign.Center
                        )
                    }

                    uiState.appSettings.isEmpty() -> {
                        Text(
                            text = stringResource(R.string.no_apps_found),
                            style = MaterialTheme.typography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.fillMaxWidth().weight(1f))
                    }

                    else -> {
                        LazyColumn(
                            modifier = Modifier.fillMaxWidth().weight(1f),
                            contentPadding = PaddingValues(
                                end = 24.dp,
                                top = 16.dp,
                                bottom = 16.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            itemsIndexed(
                                uiState.appSettings,
                                key = { _, item -> item.packageName }) { index, appItem ->
                                AppCardItem(
                                    appItem = appItem,
                                    onClick = {
                                        viewModel.toggleFavorite(appItem)
                                    },
                                    modifier = if (index == 0) Modifier.focusRequester(
                                        firstAppFocusRequester
                                    ) else Modifier
                                )
                            }
                        }
                        LaunchedEffect(uiState.appSettings) {
                            if (uiState.appSettings.isNotEmpty()) {
                                if (!firtstAppFocused.value) {
                                    firstAppFocusRequester.requestFocus()
                                }
                                firtstAppFocused.value = true
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        viewModel.setOnboarded()
                        onNext()
                    },
                    modifier = Modifier
                ) {
                    Text("Lets go home!")
                }


            }
        }

}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppCardItem(
    appItem: AppSettingItem,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(2.dp),
        shape = CardDefaults.shape(),
        scale = CardDefaults.scale(focusedScale = 1.05f)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
             val iconBitmap = remember(appItem.icon) {
                appItem.icon
            }
            if (iconBitmap != null) {
                Image(
                    bitmap = iconBitmap,
                    contentDescription = "${appItem.name} icon",
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            } else {
                 Box(
                    modifier = Modifier
                        .size(40.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = appItem.name.firstOrNull()?.uppercaseChar()?.toString() ?: "A",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
            }

            Spacer(Modifier.width(16.dp))

            // App Name
            Text(
                text = appItem.name,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            Spacer(Modifier.width(16.dp))

            // Selection Indicator (e.g., a Checkbox or custom icon)
            if (appItem.isFavorite) {
                Icon(
                    imageVector = iconHeart,
                    contentDescription = "Selected",
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(24.dp)
                )
            } else {
                Icon(
                    imageVector = iconHeartOff,
                    contentDescription = "Not Selected",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}
