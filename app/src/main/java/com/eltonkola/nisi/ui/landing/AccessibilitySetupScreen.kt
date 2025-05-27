package com.eltonkola.nisi.ui.landing


import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Button
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.eltonkola.nisi.HomeButtonAccessibilityService
import com.eltonkola.nisi.R
import com.eltonkola.nisi.isAccessibilityServiceEnabled
import com.eltonkola.nisi.ui.icons.iconPlay
import androidx.core.net.toUri

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AccessibilitySetupScreen(
    onContinueClicked: () -> Unit
) {
    val context = LocalContext.current

    // This state will update when the composable recomposes.
    // Recomposition can be triggered by returning to this screen after changing settings.
    var isServiceEnabled by remember { // Removed context key for more frequent checks on recomposition
        mutableStateOf(
            isAccessibilityServiceEnabled(context, HomeButtonAccessibilityService::class.java)
        )
    }

    // For better reactivity, especially after returning from settings,
    // you might re-check in a LaunchedEffect tied to a lifecycle event
    // or simply rely on the recomposition that happens when the activity is resumed.
    // For simplicity here, we update it in a LaunchedEffect that runs on composition and recomposition.
    LaunchedEffect(Unit) { // Re-check every time the screen composes/recomposes
        isServiceEnabled = isAccessibilityServiceEnabled(context, HomeButtonAccessibilityService::class.java)
    }
    // A more robust way to refresh after returning from settings:
    val lifecycleOwner = androidx.compose.ui.platform.LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner) {
        val observer = androidx.lifecycle.LifecycleEventObserver { _, event ->
            if (event == androidx.lifecycle.Lifecycle.Event.ON_RESUME) {
                isServiceEnabled = isAccessibilityServiceEnabled(context, HomeButtonAccessibilityService::class.java)
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }


    val openSettingsButtonFocusRequester = remember { FocusRequester() }
    val continueButtonFocusRequester = remember { FocusRequester() }

        Box(
            modifier = Modifier
                .fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(0.75f) // Slightly wider for more text
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    iconPlay, // Replace with your logo
                    contentDescription = "NISI Launcher Logo",
                    modifier = Modifier
                        .size(100.dp)
                        .padding(bottom = 24.dp),
                    contentScale = ContentScale.Fit
                )

                Text(
                    text = stringResource(R.string.accessibility_setup_title),
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 16.dp)
                )

                Text(
                    text = stringResource(R.string.accessibility_setup_description),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(bottom = 24.dp)
                )

                // Status Indicator (Optional but helpful)
                Text(
                    text = if (isServiceEnabled) stringResource(R.string.accessibility_status_enabled)
                    else stringResource(R.string.accessibility_status_disabled),
                    style = MaterialTheme.typography.titleMedium,
                    color = if (isServiceEnabled) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(bottom = 24.dp)
                )


                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                    Button(
                        onClick = {
                            openAccessibilitySettingsDirectly(context, HomeButtonAccessibilityService::class.java)
                        },
                        modifier = Modifier
                            .focusRequester(openSettingsButtonFocusRequester)
                    ) {
                        Text(stringResource(R.string.open_accessibility_settings))
                    }
                    Spacer(modifier = Modifier.size(16.dp))
                    Button(
                        onClick = onContinueClicked,
                        modifier = Modifier
                            .focusRequester(continueButtonFocusRequester)
                    ) {
                        Text(text = stringResource(R.string.skip_button))
                    }
                }
            }

    }

    LaunchedEffect(isServiceEnabled) {
        if (!isServiceEnabled) {
            openSettingsButtonFocusRequester.requestFocus()
        } else {
            continueButtonFocusRequester.requestFocus()
        }
    }
}

fun openAccessibilitySettingsDirectly(context: Context, serviceClass: Class<out android.accessibilityservice.AccessibilityService>) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK) // Essential if calling from a non-Activity context

    // The following extras are non-SDK, heuristic-based, and not guaranteed to work.
    // They attempt to tell the Settings app which fragment/service to highlight or open.
    val componentName = ComponentName(context.packageName, serviceClass.name).flattenToString()

    // This is a common key used by many Settings implementations.
    // The value is the flattened ComponentName of your service.
    intent.putExtra(":settings:fragment_args_key", componentName) // Note: Using string literal for the key

    // Some systems might also require a Bundle for the fragment arguments.
    val bundle = Bundle()
    bundle.putString(":settings:fragment_args_key", componentName)
    intent.putExtra(":settings:show_fragment_args", bundle) // Note: Using string literal for the key

    // Another variation seen for some systems:
    // intent.putExtra("EXTRA_COMPONENT_NAME", ComponentName(context.packageName, serviceClass.name))

    try {
        context.startActivity(intent)
    } catch (e: Exception) {
        // Fallback: If the direct approach fails (e.g., ActivityNotFoundException for specific extras,
        // or extras are ignored), open the general accessibility settings.
        // This is unlikely for the base ACTION_ACCESSIBILITY_SETTINGS, but good to be defensive.
        val fallbackIntent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
        fallbackIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(fallbackIntent)
    }
}
