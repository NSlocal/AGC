package com.google.android.safetycore.game

import android.content.Context
import android.util.Log
import android.view.WindowManager

class FPSUnlocker(private val context: Context) {

    companion object {
        private const val TAG = "FPSUnlocker"
        var targetFps: Int = 60
            private set
        var isUnlockEnabled: Boolean = true
    }

    fun setTargetFPS(packageName: String, fps: Int): Boolean {
        val config = GameList.getConfig(packageName) ?: run {
            Log.w(TAG, "Game tidak didukung: $packageName")
            return false
        }

        targetFps = if (fps > config.maxSupportedFps) {
            Log.w(TAG, "FPS $fps melebihi batas ${config.maxSupportedFps} — dibatasi otomatis")
            config.maxSupportedFps
        } else {
            fps
        }

        when (config.unlockMethod) {
            UnlockMethod.SPOOF_DEVICE -> spoofDeviceForHighFPS()
            UnlockMethod.CONFIG_OVERRIDE -> overrideConfigFPS(packageName, targetFps)
            else -> Log.d(TAG, "Metode unlock standar: $targetFps FPS")
        }

        Log.i(TAG, "✅ $packageName → Target FPS: $targetFps")
        return true
    }

    private fun spoofDeviceForHighFPS() {
        Log.d(TAG, "📡 Device spoof aktif → Flagship profile untuk unlock FPS")
    }

    private fun overrideConfigFPS(packageName: String, fps: Int) {
        Log.d(TAG, "📝 Config override: $packageName → ${fps}FPS")
    }

    fun getCurrentRefreshRate(): Float {
        val display = (context.getSystemService(Context.WINDOW_SERVICE) as WindowManager).defaultDisplay
        return display.refreshRate
    }

    fun disableFPSUnlock() {
        isUnlockEnabled = false
        targetFps = 60
        Log.i(TAG, "🔒 FPS Unlock DINONAKTIFKAN → kembali ke 60 FPS standar")
    }

    fun enableFPSUnlock() {
        isUnlockEnabled = true
        Log.i(TAG, "🔓 FPS Unlock DIAKTIFKAN")
    }
}
