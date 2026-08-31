package com.google.android.safetycore.util

import android.app.ActivityManager
import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.safetycore.manager.GameManager
import com.google.android.safetycore.overlay.FPSOverlayService
import com.google.android.safetycore.service.GameBoostService

class GameLaunchMonitor(private val context: Context) {
    private val handler = Handler(Looper.getMainLooper())
    private val gameManager = GameManager.getInstance(context)
    private var lastPackage: String? = null

    private val checkRunnable = object : Runnable {
        override fun run() {
            checkForegroundApp()
            handler.postDelayed(this, 1000)
        }
    }

    fun startMonitoring() {
        handler.postDelayed(checkRunnable, 500)
    }

    fun stopMonitoring() {
        handler.removeCallbacks(checkRunnable)
    }

    private fun checkForegroundApp() {
        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningApp = am.getRunningAppProcesses()?.firstOrNull { it.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND }
            ?: return
        val packageName = runningApp.processName

        if (packageName != lastPackage) {
            lastPackage = packageName
            if (gameManager.isGameSupported(packageName)) {
                val gameInfo = gameManager.getGameInfo(packageName)
                // Auto-adjust FPS target based on game
                gameInfo?.let {
                    // Apply refresh rate hint if needed
                }
            }
        }
    }
}
