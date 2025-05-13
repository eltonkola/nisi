package com.eltonkola.nisi.ui.launcher

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import coil3.compose.AsyncImage
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.eltonkola.nisi.R
import com.eltonkola.nisi.data.model.AppSettingItem
import com.eltonkola.nisi.ui.launcher.widgets.ClockWidget
import com.eltonkola.nisi.ui.launcher.widgets.weather.WeatherWidget
import com.eltonkola.nisi.ui.model.AppItemActions
import com.eltonkola.nisi.ui.model.getMenuActions

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Box(modifier = Modifier.fillMaxSize()) {

        when {
            uiState.isLoading -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            uiState.error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize().padding(48.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Error: ${uiState.error}",
                        color = MaterialTheme.colorScheme.error,
                        style = MaterialTheme.typography.headlineSmall,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {

                LauncherMainUi(
                    uiState = uiState,
                    appItemActions = viewModel.appItemActions,
                    navController = navController,
                )

            }


        }
    }

}

@Composable
private fun LauncherMainUi(
    uiState: HomeUiState,
    appItemActions: AppItemActions,
    navController: NavHostController
){
    Box(modifier = Modifier.fillMaxSize()) {
        val context = LocalContext.current

        val wallpaperIdentifier = uiState.selectedWallpaperIdentifier
        val isOffline = wallpaperIdentifier.startsWith("drawable/")
        val imageModel = ImageRequest.Builder(context)
            .data(
                if (isOffline) {
                    context.resources.getIdentifier(
                        wallpaperIdentifier.substringAfter('/'),
                        wallpaperIdentifier.substringBefore('/'),
                        context.packageName
                    ).takeIf { it != 0 } ?: R.drawable.offline_wallpaper_0
                } else {
                    wallpaperIdentifier
                }
            )
            .crossfade(true)
            .build()

        AsyncImage(
            model = imageModel,
            contentDescription = "Background Wallpaper",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop // Crop to fill the entire screen
        )



        Column(
            modifier = Modifier
                .fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.height(22.dp))


            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){

                ClockWidget(modifier = Modifier.weight(1f))

                WeatherWidget(modifier = Modifier.weight(1f))

            }
            Spacer(modifier = Modifier.size(32.dp))

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                // 3. Bottom App Bar (using TvLazyRow and ViewModel data)
                AppIconRow(
                    apps = uiState.favoriteApps, // Pass the observed list of apps
                    appItemActions = appItemActions
                )

                Spacer(modifier = Modifier.height(4.dp))

                // 4. Tabs (using BottomTab data)
                HomeSectionTabs(navController)
            }
        }
    }
}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppIconRow(
    apps: List<AppSettingItem>,
    appItemActions: AppItemActions
) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            contentPadding = PaddingValues(vertical = 16.dp, horizontal = 16.dp),
        ) {


            itemsIndexed(apps) { index, app ->

                val menuActions = remember(app) { app.getMenuActions(appItemActions) }

                AppItemUi(
                    app = app,
                    menuItems = menuActions,
                    modifier = Modifier,
                    iconsSize = 160.dp
                )
            }

        }
}


data class PopUpMenuItem(
    val id: String,
    val name: String,
    val icon: ImageVector,
)

