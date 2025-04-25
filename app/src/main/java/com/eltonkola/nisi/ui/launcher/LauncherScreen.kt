package com.eltonkola.nisi.ui.launcher

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.eltonkola.nisi.R
import com.eltonkola.nisi.data.model.App
import com.eltonkola.nisi.ui.theme.NisiTheme
import androidx.hilt.navigation.compose.hiltViewModel
import com.eltonkola.nisi.ui.launcher.widgets.ClockWidget
import com.eltonkola.nisi.ui.launcher.widgets.weather.WeatherWidget


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LauncherScreen(
    viewModel: LauncherViewModel = hiltViewModel(),
    navController: NavHostController = rememberNavController()
) {

    val context = LocalContext.current
    val apps by viewModel.apps.collectAsState()


        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background Image
            Image(
                painter = painterResource(id = R.drawable.offline_wallpaper_1), // Replace
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            // Optional: Gradient Scrim
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .fillMaxHeight(0.4f)
                    .align(Alignment.BottomCenter)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                        )
                    )
            )

            // Main content column
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 16.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(22.dp))

                ClockWidget(modifier = Modifier)
                WeatherWidget(modifier = Modifier)

                Spacer(modifier = Modifier.weight(1f))

                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // 3. Bottom App Bar (using TvLazyRow and ViewModel data)
                    AppIconRow(
                        apps = apps, // Pass the observed list of apps
                        onAppClick = { packageName -> // Handle click event
                            viewModel.launchApp(context, packageName)
                        }
                    )

                    Spacer(modifier = Modifier.height(24.dp))

                    // 4. Tabs (using BottomTab data)
                    HomeSectionTabs(navController)
                }
            }
        }

}


@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AppIconRow(
    apps: List<App>,
    onAppClick: (packageName: String) -> Unit
) {
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(20.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
        ) {

            itemsIndexed(apps) { index, app ->
                AppItemUi(
                    app = app,
                    onClick = { onAppClick(app.packageName) },
                    modifier = Modifier
                )
            }

        }
}

@Preview
@Composable
fun ClockWeatherWidgetPreview() {
    NisiTheme {
        Box(Modifier.background(Color.DarkGray).padding(16.dp)) {
            ClockWidget()
        }
    }
}

@Preview
@Composable
fun AppIconRowPreview() {
    val sampleApps = remember {
        listOf(
            App("App One", "pkg1", null),
            App("App Two", "pkg2", null)
        )
    }
    NisiTheme {
        Box(Modifier.background(Color.DarkGray).padding(16.dp)) {
            AppIconRow(apps = sampleApps, onAppClick = {})
        }
    }
}


