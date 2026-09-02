package com.google.android.safetycore.overlay

import android.content.Context
import android.content.SharedPreferences

object OverlayManager {
    private const val PREFS_NAME = "SafetyCorePrefs"
    const val SHOW_FPS = "show_fps"
    private lateinit var prefs: SharedPreferences

    fun init(context: Context) {
        prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun isShowFpsEnabled(): Boolean = prefs.getBoolean(SHOW_FPS, false)

    fun setShowFpsEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(SHOW_FPS, enabled).apply()
    }
}
