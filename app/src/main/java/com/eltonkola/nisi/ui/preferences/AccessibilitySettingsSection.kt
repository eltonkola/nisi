package com.eltonkola.nisi.ui.preferences

import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Text
import com.eltonkola.nisi.HomeButtonAccessibilityService
import com.eltonkola.nisi.isAccessibilityServiceEnabled
import com.eltonkola.nisi.promptEnableAccessibilityService

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AccessibilitySettingsSection() {
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    var isServiceEnabled  by remember(context) {
        mutableStateOf(
            isAccessibilityServiceEnabled(context, HomeButtonAccessibilityService::class.java)
        )
    }


    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
    Text("Accessibility settings", style = androidx.tv.material3.MaterialTheme.typography.headlineSmall)
    Text("In order to use the app as the main launcher, you need to enable accessibility service. This will allow the app to override the home button press and launch the app.", style = androidx.tv.material3.MaterialTheme.typography.bodyMedium)

    Spacer(modifier = Modifier.size(4.dp))

        DisposableEffect(lifecycleOwner) {
            val observer = LifecycleEventObserver { _, event ->
                // Re-check the status when the lifecycle hits RESUME
                if (event == Lifecycle.Event.ON_RESUME) {
                    Log.d("AccessibilitySettings", "Lifecycle ON_RESUME: Re-checking accessibility service status.")
                    isServiceEnabled = isAccessibilityServiceEnabled(context, HomeButtonAccessibilityService::class.java)
                }
            }

            // Add the observer to the lifecycle
            lifecycleOwner.lifecycle.addObserver(observer)

            // Remove the observer when the composable leaves the composition
            onDispose {
                lifecycleOwner.lifecycle.removeObserver(observer)
            }
        }

    if(!isServiceEnabled ){
        Button(onClick = {
            promptEnableAccessibilityService(context)
        }) {
            Text("Enable Accessibility Service")
        }
    }else{
        Button(onClick = {
            promptEnableAccessibilityService(context)
        }) {
            Text("Accessibility Services already enabled!")
        }
    }

    }
}
