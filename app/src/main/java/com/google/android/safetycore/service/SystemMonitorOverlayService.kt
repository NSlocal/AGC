package com.google.android.safetycore.service

import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.IBinder
import com.google.android.safetycore.monitor.SystemMonitor

class SystemMonitorOverlayService : Service() {
    companion object {
        const val PREF_MONITOR_ENABLED = "system_monitor_enabled"
        fun isSystemMonitorEnabled(context: Context): Boolean {
            return context.getSharedPreferences("SafetyCorePrefs", Context.MODE_PRIVATE)
                .getBoolean(PREF_MONITOR_ENABLED, false)
        }
    }

    private var systemMonitor: SystemMonitor? = null

    override fun onCreate() {
        super.onCreate()
        if (isSystemMonitorEnabled(this)) {
            systemMonitor = SystemMonitor(this)
            systemMonitor?.start()
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        systemMonitor?.stop()
        super.onDestroy()
    }
}
