package com.google.android.safetycore

import android.app.Application
import android.util.Log

class SafetyCoreApp : Application() {
    companion object {
        const val TAG = "SafetyCore"
        lateinit var instance: SafetyCoreApp private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
        Log.d(TAG, "SafetyCore initialized")
    }
}
