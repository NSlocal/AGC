package com.google.android.safetycore

import android.app.Application
import android.util.Log

class SafetyCoreApp : Application() {
    companion object {
        private const val TAG = "SafetyCoreApp"
        lateinit var instance: SafetyCoreApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "✅ SafetyCore App Initialized")
    }
}
