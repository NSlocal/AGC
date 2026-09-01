package com.google.android.safetycore.monitor

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.provider.Settings
import com.google.android.safetycore.ui.SettingsActivity

object GameBooster {
    private val GAME_PACKAGES = mapOf(
        "com.tencent.tmgp.speedmobile" to "QQ飞车",
        "com.garena.game.fctw" to "Speed Drifters"
    )

    val supportedGames: List<String>
        get() = GAME_PACKAGES.keys.toList()

    fun isGameRunning(context: Context): Boolean {
        val pm = context.packageManager
        return GAME_PACKAGES.keys.any { pkg ->
            try {
                pm.getPackageInfo(pkg, 0)
                @Suppress("DEPRECATION")
                val running = (context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager)
                    .runningAppProcesses
                running.any { it.processName == pkg }
            } catch (e: Exception) { false }
        }
    }

    fun getCurrentGame(context: Context): String? {
        GAME_PACKAGES.forEach { (pkg, name) ->
            try {
                context.packageManager.getPackageInfo(pkg, 0)
                @Suppress("DEPRECATION")
                val running = (context.getSystemService(Context.ACTIVITY_SERVICE) as android.app.ActivityManager)
                    .runningAppProcesses
                if (running.any { it.processName == pkg }) return name
            } catch (e: Exception) {}
        }
        return null
    }

    fun setFpsLimit(context: Context, fps: Int) {
        if (!SettingsActivity.isFPSBoostEnabled(context)) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                Settings.Global.putFloat(context.contentResolver, "fps_limit", fps.toFloat())
            }
        } catch (e: Exception) { }
    }

    fun optimizePerformance(context: Context) {
        if (!SettingsActivity.isGameBoostEnabled(context)) return
        setFpsLimit(context, 120)
    }
}
