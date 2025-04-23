package com.eltonkola.nisi.ui

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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.eltonkola.nisi.R
import com.eltonkola.nisi.model.App
import com.eltonkola.nisi.ui.theme.NisiTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun LauncherScreen(viewModel: LauncherViewModel = viewModel()) { // Provide default for previews

    val context = LocalContext.current
    val apps by viewModel.apps // Observe the apps state

    LaunchedEffect(Unit) {
        viewModel.loadInstalledApps(context)
    }


        Box(modifier = Modifier.fillMaxSize()) {
            // 1. Background Image
            Image(
                painter = painterResource(id = R.drawable.placeholder_background), // Replace
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
                    .padding(bottom = 32.dp),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Spacer(modifier = Modifier.height(64.dp))
                // 2. Center Clock and Weather (Placeholder)
                ClockWeatherWidget() // Keep this as is for now
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
                    HomeSectionTabs()
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

@Preview(device = "id:tv_1080p")
@Composable
fun LauncherScreenPreview() {
    LauncherScreen(viewModel = LauncherViewModel()) // Basic preview with default ViewModel
}

@Preview
@Composable
fun ClockWeatherWidgetPreview() {
    NisiTheme {
        Box(Modifier.background(Color.DarkGray).padding(16.dp)) {
            ClockWeatherWidget()
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


