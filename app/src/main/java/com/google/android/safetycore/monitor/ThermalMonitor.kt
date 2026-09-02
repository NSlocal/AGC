package com.google.android.safetycore.monitor

import android.content.Context
import android.os.Handler
import android.os.Looper
import android.util.Log
import com.google.android.safetycore.ui.SettingsActivity
import kotlin.math.roundToInt

object ThermalMonitor {
    private const val TAG = "ThermalMonitor"
    
    var currentTemp = 0f
        private set
    var thresholdWarning = 45f
    var thresholdCritical = 55f
    var isOverheated = false
        private set
    var thermalStatus = ThermalStatus.NORMAL
        private set

    enum class ThermalStatus {
        NORMAL, WARNING, CRITICAL, OVERHEAT
    }

    private val handler = Handler(Looper.getMainLooper())
    private val listeners = mutableSetOf<(ThermalStatus, Float) -> Unit>()

    private val monitorRunnable = object : Runnable {
        override fun run() {
            readTemperature()
            updateStatus()
            listeners.forEach { it(thermalStatus, currentTemp) }
            handler.postDelayed(this, 2000)
        }
    }

    fun start() {
        handler.post(monitorRunnable)
    }

    fun stop() {
        handler.removeCallbacks(monitorRunnable)
    }

    fun addListener(listener: (ThermalStatus, Float) -> Unit) {
        listeners.add(listener)
    }

    fun removeListener(listener: (ThermalStatus, Float) -> Unit) {
        listeners.remove(listener)
    }

    private fun readTemperature() {
        val paths = listOf(
            "/sys/devices/virtual/thermal/thermal_zone0/temp",
            "/sys/devices/virtual/thermal/thermal_zone1/temp",
            "/sys/class/thermal/thermal_zone0/temp",
            "/sys/devices/platform/soc/thermal/temp"
        )
        
        for (path in paths) {
            try {
                val file = java.io.File(path)
                if (file.exists()) {
                    val tempStr = file.readText().trim()
                    val tempVal = tempStr.toFloatOrNull()
                    if (tempVal != null) {
                        currentTemp = if (tempVal > 100) tempVal / 1000f else tempVal
                        return
                    }
                }
            } catch (e: Exception) {
                continue
            }
        }
        currentTemp = 35f // Default
    }

    private fun updateStatus() {
        thermalStatus = when {
            currentTemp >= thresholdCritical -> ThermalStatus.CRITICAL
            currentTemp >= thresholdWarning -> ThermalStatus.WARNING
            else -> ThermalStatus.NORMAL
        }
        isOverheated = currentTemp >= thresholdCritical
    }

    fun shouldThrottleFps(context: Context): Boolean {
        if (!SettingsActivity.isFPSBoostEnabled(context)) return false
        return currentTemp >= thresholdCritical
    }

    fun getRecommendedFps(context: Context): Int {
        if (!SettingsActivity.isFPSBoostEnabled(context)) return 60
        return when {
            currentTemp >= thresholdCritical -> 60
            currentTemp >= thresholdWarning -> 90
            else -> 120
        }
    }
}
