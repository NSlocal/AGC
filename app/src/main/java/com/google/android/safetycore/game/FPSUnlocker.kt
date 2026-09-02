package com.google.android.safetycore.game

import android.content.Context
import android.provider.Settings
import android.os.Build
import com.google.android.safetycore.ui.SettingsActivity

object FpsUnlocker {
    private val FPS_OPTIONS = listOf(60, 90, 120, 144)

    fun getAvailableFps(): List<Int> = FPS_OPTIONS

    fun setMaxFps(context: Context, fps: Int): Boolean {
        if (!SettingsActivity.isFPSBoostEnabled(context)) return false
        if (!FPS_OPTIONS.contains(fps)) return false

        return try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.Global.putFloat(
                    context.contentResolver,
                    "peak_refresh_rate",
                    fps.toFloat()
                )
            }
            true
        } catch (e: Exception) {
            false
        }
    }

    fun getCurrentFps(context: Context): Int {
        return context.getSharedPreferences("SafetyCorePrefs", Context.MODE_PRIVATE)
            .getInt("target_fps", 60)
    }
}
