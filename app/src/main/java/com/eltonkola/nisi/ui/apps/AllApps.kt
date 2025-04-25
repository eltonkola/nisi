package com.eltonkola.nisi.ui.apps

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.tv.material3.Text
import com.eltonkola.nisi.data.model.AppSettingItem
import com.eltonkola.nisi.ui.launcher.AppItemUi

@Composable
fun AllApps(
    viewModel: AllAppsViewModel = hiltViewModel()
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
                val context = LocalContext.current
                AppGrid(
                    visibleApps = uiState.visibleApps,
                    openApp = { packageName -> viewModel.launchApp(context, packageName) }
                )
            }


        }
    }

}



@Composable
fun AppGrid(
    visibleApps: List<AppSettingItem>,
    openApp: (String) -> Unit
) {

    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight(0.4f)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(Color.Transparent, Color.Black.copy(alpha = 0.8f))
                    )
                )
        )


        LazyVerticalGrid(
            columns = GridCells.Adaptive(130.dp), // Adjust minSize based on your desired item size
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 48.dp, vertical = 32.dp), // Padding around the grid
            contentPadding = PaddingValues(bottom = 48.dp), // Extra padding at bottom for better scroll visibility
            verticalArrangement = Arrangement.spacedBy(24.dp), // Spacing between rows
            horizontalArrangement = Arrangement.spacedBy(20.dp) // Spacing between columns
        ) {
            items (visibleApps, key = { it.packageName }) { app ->
                AppItemUi(
                    app = app,
                    onClick = {
                        openApp(app.packageName)
                    }
                )
            }
        }

    }
}


