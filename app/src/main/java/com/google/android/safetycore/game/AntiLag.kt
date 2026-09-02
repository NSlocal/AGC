package com.google.android.safetycore.game

import android.content.Context
import android.os.Handler
import android.os.Looper
import com.google.android.safetycore.ui.SettingsActivity

object AntiLag {
    private val handler = Handler(Looper.getMainLooper())
    private var isActive = false
    private var currentContext: Context? = null

    private val monitorRunnable = object : Runnable {
        override fun run() {
            val context = currentContext ?: return
            if (!SettingsActivity.isGameBoostEnabled(context)) {
                stop()
                return
            }
            optimizeMemory()
            handler.postDelayed(this, 5000)
        }
    }

    fun start(context: Context) {
        if (isActive || !SettingsActivity.isGameBoostEnabled(context)) return
        currentContext = context
        isActive = true
        handler.post(monitorRunnable)
    }

    fun stop() {
        isActive = false
        handler.removeCallbacks(monitorRunnable)
    }

    private fun optimizeMemory() {
        Runtime.getRuntime().gc()
        System.runFinalization()
    }
}
