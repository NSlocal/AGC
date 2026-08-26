package com.google.android.safetycore

import android.content.Context
import android.content.SharedPreferences

object SafetyCoreService {
    private const val PREF_NAME = "SafetyCorePrefs"
    private const val KEY_ENABLED = "global_enabled"

    fun isEnabled(context: Context): Boolean {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        return prefs.getBoolean(KEY_ENABLED, false)
    }

    fun setEnabled(context: Context, enabled: Boolean) {
        val prefs: SharedPreferences = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean(KEY_ENABLED, enabled).apply()
    }
}
