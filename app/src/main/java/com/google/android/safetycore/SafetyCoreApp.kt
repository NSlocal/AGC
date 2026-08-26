package com.google.android.safetycore

import android.app.Application
import android.util.Log

class SafetyCoreApp : Application() {
    companion object {
        lateinit var instance: SafetyCoreApp
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        try {
            Log.d("SafetyCoreApp", "✅ App Initialized")
        } catch (e: Exception) {
            Log.e("SafetyCoreApp", "⚠️ Init error: ${e.message}")
        }
    }
}
