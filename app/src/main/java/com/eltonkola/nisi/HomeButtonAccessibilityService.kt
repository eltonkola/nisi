package com.eltonkola.nisi

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.ServiceInfo
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.text.TextUtils
import android.util.Log
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager
import android.widget.Toast

class HomeButtonAccessibilityService : AccessibilityService() {

    private var isEnabled = false
    private val handler = Handler(Looper.getMainLooper())
    private var lastHomePressTimes = ArrayList<Long>()
    private val HOME_PRESS_COOLDOWN_MS = 500 // prevent multiple triggers in short succession

    override fun onServiceConnected() {
        val info = serviceInfo
        info.flags = info.flags or AccessibilityServiceInfo.FLAG_REQUEST_FILTER_KEY_EVENTS
        serviceInfo = info
        isEnabled = true
        Log.d(">>>>> HomeButtonAccessibilityService", "OnConnected")
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // This is required to implement AccessibilityService but we don't need any logic here
        Log.d(">>>>> HomeButtonAccessibilityService", "onAccessibilityEvent $event")
    }

    override fun onInterrupt() {
        // This is required to implement AccessibilityService
        Log.d(">>>>> HomeButtonAccessibilityService", "OnInterrupt")
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (!isEnabled) return false

       // Toast.makeText(applicationContext, "Event key: ${event.keyCode}", Toast.LENGTH_SHORT).show()
        Log.d(">>>>> HomeButtonAccessibilityService", "Event key: ${event.keyCode}")

        // Check if the HOME button is pressed
        if (event.keyCode == KeyEvent.KEYCODE_HOME && event.action == KeyEvent.ACTION_UP) {
            val currentTime = System.currentTimeMillis()

            // Clean up old timestamps
            lastHomePressTimes.removeAll { currentTime - it > HOME_PRESS_COOLDOWN_MS }

            // If we've had a recent home press, ignore this one to prevent multiple launches
            if (lastHomePressTimes.isNotEmpty()) {
                return true
            }

            // Record this home press
            lastHomePressTimes.add(currentTime)

            // Launch our launcher activity
            handler.post {
                Log.d(">>>>> HomeButtonAccessibilityService", "Launching main activity")
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
                Toast.makeText(applicationContext, "Launching main activity", Toast.LENGTH_SHORT).show()
            }

            // Return true to consume the event
            return true
        }

        return false
    }
}


fun promptEnableAccessibilityService(context: Context) {
    val intent = Intent(Settings.ACTION_ACCESSIBILITY_SETTINGS)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)

    AlertDialog.Builder(context)
        .setTitle("Enable Accessibility Service")
        .setMessage("To use this app as your home launcher, please enable the accessibility service.")
        .setPositiveButton("Go to Settings") { _, _ ->
            context.startActivity(intent)
        }
        .setNegativeButton("Cancel", null)
        .show()
}

/**
 * Checks if a specific Accessibility Service is enabled in the system settings.
 *
 * This function uses two methods for robustness:
 * 1. Querying AccessibilityManager (preferred, but can have caching issues).
 * 2. Reading Settings.Secure (fallback, might not always be reliable/accessible).
 *
 * @param context The application context.
 * @param serviceClass The Class object of the Accessibility Service to check (e.g., YourAccessibilityService::class.java).
 * @return True if the service is enabled, false otherwise.
 */
fun isAccessibilityServiceEnabled(context: Context, serviceClass: Class<*>): Boolean {
    val expectedComponentName = ComponentName(context, serviceClass)
    val expectedPackageName = expectedComponentName.packageName
    val expectedClassName = expectedComponentName.className
    val expectedFlattenedName = expectedComponentName.flattenToString() // For Settings.Secure check

    Log.d("AccessibilityCheck", "Checking for service: $expectedFlattenedName")

    // --- Method 1: Using AccessibilityManager ---
    try {
        val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as? AccessibilityManager
        if (accessibilityManager?.isEnabled == true) {
            val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            Log.d("AccessibilityCheck", "AccessibilityManager is enabled. Found ${enabledServices.size} enabled services.")

            for (serviceInfo in enabledServices) {
                val resolvedServiceInfo: ServiceInfo? = serviceInfo.resolveInfo?.serviceInfo
                if (resolvedServiceInfo != null) {
                    // Construct ComponentName from package and class name in ServiceInfo
                    val servicePackageName = resolvedServiceInfo.packageName
                    val serviceClassName = resolvedServiceInfo.name // This is the fully qualified class name

                    // Check if names are valid before comparing
                    if (!servicePackageName.isNullOrEmpty() && !serviceClassName.isNullOrEmpty()) {
                        Log.v("AccessibilityCheck", "Checking against enabled service: Pkg=$servicePackageName, Class=$serviceClassName")
                        if (servicePackageName == expectedPackageName && serviceClassName == expectedClassName) {
                            Log.i("AccessibilityCheck", "Service $expectedFlattenedName is ENABLED (via Manager - Name Match).")
                            return true // Found a match!
                        }
                    } else {
                        Log.w("AccessibilityCheck", "Resolved ServiceInfo has null/empty package or class name for service ID: ${serviceInfo.id}")
                    }
                } else {
                    // Fallback using serviceInfo.id if resolveInfo or serviceInfo is null
                    val serviceId = serviceInfo.id
                    if (serviceId != null) {
                        Log.v("AccessibilityCheck", "ResolveInfo/ServiceInfo was null. Checking against enabled service ID: $serviceId (fallback)")
                        // Compare the ID string directly against the flattened component name (less reliable but better than contains)
                        if (serviceId.equals(expectedFlattenedName, ignoreCase = true)) {
                            Log.w("AccessibilityCheck", "Service $expectedFlattenedName potentially ENABLED (via ID match).")
                            return true // Found a match via ID fallback
                        }
                    }
                }
            }
            // If loop finishes without finding the service via AccessibilityManager
            Log.d("AccessibilityCheck", "Service $expectedFlattenedName not found in AccessibilityManager list.")

        } else {
            Log.w("AccessibilityCheck", "AccessibilityManager is null or globally disabled.")
        }
    } catch (e: Exception) {
        Log.e("AccessibilityCheck", "Error querying AccessibilityManager", e)
        // Don't return yet, try the Settings.Secure method
    }

    // --- Method 2: Checking Settings.Secure (Fallback) ---
    Log.d("AccessibilityCheck", "Trying Settings.Secure method...")
    try {
        val enabledServicesSetting = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        Log.v("AccessibilityCheck", "Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES: $enabledServicesSetting")

        if (!TextUtils.isEmpty(enabledServicesSetting)) {
            val colonSplitter = TextUtils.SimpleStringSplitter(':')
            colonSplitter.setString(enabledServicesSetting)
            while (colonSplitter.hasNext()) {
                val componentNameString = colonSplitter.next()
                Log.v("AccessibilityCheck", "Checking component from setting: $componentNameString")
                // Compare the component string from settings with the expected flattened name
                if (componentNameString.equals(expectedFlattenedName, ignoreCase = true)) {
                    Log.i("AccessibilityCheck", "Service $expectedFlattenedName is ENABLED (via Settings.Secure).")
                    return true // Found a match!
                }
            }
            // If loop finishes without finding the service via Settings.Secure
            Log.d("AccessibilityCheck", "Service $expectedFlattenedName not found in Settings.Secure list.")
        } else {
            Log.d("AccessibilityCheck", "Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES is null or empty.")
        }
    } catch (e: Exception) {
        Log.e("AccessibilityCheck", "Error reading Settings.Secure", e)
        // Cannot determine state from this method
    }

    // If neither method found the service as enabled
    Log.w("AccessibilityCheck", "Service $expectedFlattenedName is DISABLED (checked both methods).")
    return false
}