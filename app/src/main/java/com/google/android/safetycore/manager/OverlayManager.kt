package com.google.android.safetycore.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.android.safetycore.model.OverlayConfig
import com.google.android.safetycore.util.PreferenceKeys

class OverlayManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("overlay_prefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var instance: OverlayManager? = null

        fun getInstance(context: Context): OverlayManager {
            return instance ?: synchronized(this) {
                instance ?: OverlayManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun getConfig(): OverlayConfig {
        return OverlayConfig(
            showFps = prefs.getBoolean(PreferenceKeys.SHOW_FPS, true),
            showCpu = prefs.getBoolean(PreferenceKeys.SHOW_CPU, true),
            showGpu = prefs.getBoolean(PreferenceKeys.SHOW_GPU, true),
            showTemp = prefs.getBoolean(PreferenceKeys.SHOW_TEMP, true),
            showBattery = prefs.getBoolean(PreferenceKeys.SHOW_BATTERY, true),
            textSize = prefs.getFloat(PreferenceKeys.TEXT_SIZE, 16f),
            bgOpacity = prefs.getInt(PreferenceKeys.BG_OPACITY, 204),
            positionX = prefs.getInt("pos_x", 20),
            positionY = prefs.getInt("pos_y", 80),
            autoRefresh = prefs.getBoolean("auto_refresh", true),
            updateIntervalMs = prefs.getLong("update_interval", 500)
        )
    }

    fun saveConfig(config: OverlayConfig) {
        prefs.edit().apply {
            putBoolean(PreferenceKeys.SHOW_FPS, config.showFps)
            putBoolean(PreferenceKeys.SHOW_CPU, config.showCpu)
            putBoolean(PreferenceKeys.SHOW_GPU, config.showGpu)
            putBoolean(PreferenceKeys.SHOW_TEMP, config.showTemp)
            putBoolean(PreferenceKeys.SHOW_BATTERY, config.showBattery)
            putFloat(PreferenceKeys.TEXT_SIZE, config.textSize)
            putInt(PreferenceKeys.BG_OPACITY, config.bgOpacity)
            putInt("pos_x", config.positionX)
            putInt("pos_y", config.positionY)
            putBoolean("auto_refresh", config.autoRefresh)
            putLong("update_interval", config.updateIntervalMs)
            apply()
        }
    }

    fun resetPosition() {
        prefs.edit().putInt("pos_x", 20).putInt("pos_y", 80).apply()
    }
}
