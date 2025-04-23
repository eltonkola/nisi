package com.eltonkola.nisi.ui.preferences

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AboutSettingsSection() {
    val context = LocalContext.current
    val version = try {
        val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
        packageInfo.versionName
    } catch (e: Exception) {
        "N/A"
    }

    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        Text("About Nisi Launcher", style = MaterialTheme.typography.headlineMedium)
        // Add your launcher icon here if desired
        // Image(painterResource(id = R.drawable.ic_launcher_foreground), ...)
        Text("Version: $version")
        Text("A custom launcher experience.")
        // Add links to source code, website, acknowledgements etc.
    }
}