package com.google.android.safetycore.game

import android.content.Context
import android.os.Build
import android.os.PowerManager
import com.google.android.safetycore.ui.SettingsActivity

object GameOptimizer {
    private const val TAG = "GameOptimizer"

    fun applyGameMode(context: Context, gamePackage: String) {
        if (!SettingsActivity.isGameBoostEnabled(context)) return

        when (gamePackage) {
            "com.tencent.tmgp.speedmobile",
            "com.garena.game.fctw" -> {
                setPerformanceMode(context, true)
                setFpsPreference(context, 120)
            }
        }
    }

    fun setPerformanceMode(context: Context, enable: Boolean) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            val powerMgr = context.getSystemService(Context.POWER_SERVICE) as PowerManager
            if (enable) {
                powerMgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "SafetyCore:GameBoost").apply {
                    acquire(30 * 60 * 1000L)
                }
            }
        }
    }

    fun setFpsPreference(context: Context, fps: Int) {
        context.getSharedPreferences("SafetyCorePrefs", Context.MODE_PRIVATE)
            .edit().putInt("target_fps", fps).apply()
    }

    fun getSupportedGames(): List<GameInfo> = listOf(
        GameInfo("com.tencent.tmgp.speedmobile", "QQ飞车", 120),
        GameInfo("com.garena.game.fctw", "Speed Drifters", 120)
    )
}

data class GameInfo(
    val packageName: String,
    val name: String,
    val maxFps: Int
)
