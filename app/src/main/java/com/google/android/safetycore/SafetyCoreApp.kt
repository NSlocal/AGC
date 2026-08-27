package com.google.android.safetycore

import android.app.Application

class SafetyCoreApp : Application() {
    override fun onCreate() {
        super.onCreate()
        instance = this
    }

    companion object {
        lateinit var instance: SafetyCoreApp
            private set
    }
}
