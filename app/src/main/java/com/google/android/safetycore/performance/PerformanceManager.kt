package com.google.android.safetycore.performance

import android.os.BatteryManager

class PerformanceManager {
    fun getBatteryTemp(batteryManager: BatteryManager): Int {
        return batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_TEMPERATURE)
    }
}
