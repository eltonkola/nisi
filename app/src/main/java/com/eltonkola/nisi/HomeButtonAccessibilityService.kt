package com.eltonkola.nisi

import android.accessibilityservice.AccessibilityService
import android.accessibilityservice.AccessibilityServiceInfo
import android.app.AlertDialog
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.provider.Settings
import android.view.KeyEvent
import android.view.accessibility.AccessibilityEvent
import android.view.accessibility.AccessibilityManager

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
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent) {
        // This is required to implement AccessibilityService but we don't need any logic here
    }

    override fun onInterrupt() {
        // This is required to implement AccessibilityService
    }

    override fun onKeyEvent(event: KeyEvent): Boolean {
        if (!isEnabled) return false

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
                val intent = Intent(applicationContext, MainActivity::class.java)
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                intent.addCategory(Intent.CATEGORY_HOME)
                startActivity(intent)
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



fun isAccessibilityServiceEnabled(context: Context): Boolean {
    val accessibilityManager = context.getSystemService(Context.ACCESSIBILITY_SERVICE) as AccessibilityManager
    val enabledServices = accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_GENERIC)

    val myServiceName = ComponentName(context, HomeButtonAccessibilityService::class.java).flattenToString()

    for (service in enabledServices) {
        val serviceId = service.id
        if (serviceId.contains(myServiceName)) {
            return true
        }
    }

    return false
}
