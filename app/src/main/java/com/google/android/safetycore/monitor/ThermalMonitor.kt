package com.google.android.safetycore.monitor

import android.content.Context
import android.content.SharedPreferences

class ThermalMonitor(context: Context) {
    private val prefs = context.getSharedPreferences("SafetyCorePrefs", Context.MODE_PRIVATE)

    var isFPSBoostEnabled: Boolean
        get() = prefs.getBoolean("fps_boost", false)
        set(value) = prefs.edit().putBoolean("fps_boost", value).apply()

    fun checkThermalStatus(): Int {
        return 0
    }
}
