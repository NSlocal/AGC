package com.google.android.safetycore.overlay

import android.content.Context
import android.content.SharedPreferences
import com.google.android.safetycore.manager.AppNotificationManager
import com.google.android.safetycore.util.PreferenceKeys

class FpsDropAlertHelper(context: Context) {
    private val prefs = context.getSharedPreferences("overlay_prefs", Context.MODE_PRIVATE)
    private val notifManager = AppNotificationManager(context)
    private var belowThresholdCount = 0
    private var lastAlertTime = 0L

    private val alertThreshold: Int
        get() = prefs.getInt("fps_alert_threshold", 45)
    private val enableAlert: Boolean
        get() = prefs.getBoolean("enable_fps_alert", true)
    private val alertDuration: Int
        get() = prefs.getInt("alert_duration_sec", 3)

    fun checkFps(currentFps: Int) {
        if (!enableAlert) return
        if (currentFps < alertThreshold) {
            belowThresholdCount++
            if (belowThresholdCount >= alertDuration &&
                System.currentTimeMillis() - lastAlertTime > 30000) {
                notifManager.sendFpsDropAlert(currentFps, alertThreshold)
                lastAlertTime = System.currentTimeMillis()
                belowThresholdCount = 0
            }
        } else {
            belowThresholdCount = 0
        }
    }
}
