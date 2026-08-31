package com.google.android.safetycore.manager

import android.content.Context
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import com.google.android.safetycore.SafetyCoreApp
import kotlin.math.roundToInt

class PerformanceManager private constructor(context: Context) {
    private val batteryManager = context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
    private val handler = Handler(Looper.getMainLooper())

    companion object {
        @Volatile private var instance: PerformanceManager? = null
        fun getInstance(context: Context): PerformanceManager {
            return instance ?: synchronized(this) {
                instance ?: PerformanceManager(context.applicationContext).also { instance = it }
            }
        }
    }

    fun getBatteryLevel(): Int = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)

    fun getBatteryTemp(): Float {
        val temp = batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_TEMPERATURE)
        return if (temp != Integer.MIN_VALUE) temp / 10f else 25f
    }

    fun getCpuUsage(): Int {
        // Simulated real-time CPU usage — replace with actual implementation if needed
        return (25..85).random()
    }

    fun getGpuUsage(): Int {
        // Simulated GPU usage — replace with actual implementation on supported devices
        return (20..90).random()
    }

    fun getDeviceTemp(): Float {
        val batteryTemp = getBatteryTemp()
        // Adjust based on typical device delta
        return (batteryTemp + 2.5f).coerceIn(25f, 50f)
    }

    fun getDeviceInfo(): DeviceInfo {
        return DeviceInfo(
            manufacturer = Build.MANUFACTURER,
            model = Build.MODEL,
            androidVersion = Build.VERSION.RELEASE,
            sdkLevel = Build.VERSION.SDK_INT,
            board = Build.BOARD,
            hardware = Build.HARDWARE
        )
    }
}

data class DeviceInfo(
    val manufacturer: String,
    val model: String,
    val androidVersion: String,
    val sdkLevel: Int,
    val board: String,
    val hardware: String
)
