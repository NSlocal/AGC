package com.google.android.safetycore.game

import android.content.Context
import android.content.SharedPreferences

class GameBooster(context: Context) {
    private val prefs = context.getSharedPreferences("SafetyCorePrefs", Context.MODE_PRIVATE)

    var isFPSBoostEnabled: Boolean
        get() = prefs.getBoolean("fps_boost", false)
        set(value) = prefs.edit().putBoolean("fps_boost", value).apply()

    fun applyBoost() {
        if (isFPSBoostEnabled) {
            // logika boost FPS
        }
    }
}
