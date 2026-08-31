package com.google.android.safetycore

import android.app.Application
import android.util.Log
import com.google.android.safetycore.manager.AppNotificationManager
import com.google.android.safetycore.util.ThemeUtil

class SafetyCoreApp : Application() {
    companion object {
        private const val TAG = "SafetyCoreApp"
        lateinit var instance: SafetyCoreApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "SafetyCore Pro Initialized")
        
        // Create all notification channels for Android 8.0+
        AppNotificationManager(this).createAllChannels()
        
        // Apply saved theme on app launch
        ThemeUtil.applyTheme(this)
    }
}
