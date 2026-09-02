package com.google.android.safetycore.game

import android.content.Context
import android.util.Log

class GameBooster(private val context: Context) {

    private val fpsUnlocker = FpsUnlocker
    private val antiLag = AntiLag

    var isGameBoosterEnabled: Boolean = true
        set(value) {
            field = value
            Log.i("GameBooster", if (value) "🎮 Game Booster AKTIF" else "🎮 Game Booster DINONAKTIF")
        }

    var fpsUnlockEnabled: Boolean = true
        set(value) {
            field = value
            if (!value) fpsUnlocker.disableFPSUnlock()
            else fpsUnlocker.enableFPSUnlock()
        }

    var antiLagEnabled: Boolean = true
        set(value) {
            field = value
            AntiLag.antiLagEnabled = value
        }

    var thermalBypassEnabled: Boolean = true
        set(value) {
            field = value
            AntiLag.thermalBypassEnabled = value
        }

    var performanceModeEnabled: Boolean = true
        set(value) {
            field = value
            AntiLag.performanceMode = value
        }

    fun onGameLaunch(packageName: String): Boolean {
        if (!isGameBoosterEnabled || !GameList.isSupported(packageName)) return false
        Log.i("GameBooster", "🎮 Game terdeteksi: $packageName")
        val config = GameList.getConfig(packageName) ?: return false

        if (fpsUnlockEnabled) fpsUnlocker.setTargetFPS(packageName, config.maxSupportedFps)
        if (antiLagEnabled) antiLag.applyGameOptimizations(packageName)
        return true
    }

    fun onGameExit(packageName: String) {
        antiLag.stopAllOptimizations()
        Log.i("GameBooster", "👋 Game ditutup: $packageName — kembalikan setting normal")
    }

    fun disableAllGameFeatures() {
        isGameBoosterEnabled = false
        fpsUnlockEnabled = false
        antiLagEnabled = false
        thermalBypassEnabled = false
        performanceModeEnabled = false
        fpsUnlocker.disableFPSUnlock()
        antiLag.stopAllOptimizations()
        Log.w("GameBooster", "⚠️ SEMUA fitur Game Booster DINONAKTIFKAN")
    }

    fun enableAllGameFeatures() {
        isGameBoosterEnabled = true
        fpsUnlockEnabled = true
        antiLagEnabled = true
        thermalBypassEnabled = true
        performanceModeEnabled = true
        Log.i("GameBooster", "✅ SEMUA fitur Game Booster DIAKTIFKAN")
    }

    fun getSupportedGames() = GameList.supportedGames
}
