package com.google.android.safetycore
import android.app.Application
import android.util.Log

class SafetyCoreApp : Application() {
    companion object {
        lateinit var instance: SafetyCoreApp
            private set
        const val TAG = "SafetyCorePro"
        var isDarkMode = true
        var refreshRate = 120f
    }
    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "SafetyCore Pro Initialized")
    }
}
