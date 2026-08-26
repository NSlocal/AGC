package com.google.android.safetycore

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Binder
import android.os.IBinder
import android.util.Log

class SafetyCoreService : Service() {
    companion object {
        private const val TAG = "SafetyCoreService"
        private const val PREFS_NAME = "safety_core_prefs"
        private const val KEY_ENABLED = "is_enabled"
        var isServiceEnabled: Boolean = true
            private set
        fun setEnabled(ctx: Context, enabled: Boolean) {
            isServiceEnabled = enabled
            ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_ENABLED, enabled).apply()
            Log.d(TAG, "SafetyCore ${if(enabled) "ON" else "OFF"}")
        }
        fun isEnabled(ctx: Context): Boolean =
            ctx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_ENABLED, true)
    }
    inner class LocalBinder : Binder() { fun get() = this@SafetyCoreService }
    private val binder = LocalBinder()
    override fun onCreate() {
        super.onCreate()
        isServiceEnabled = isEnabled(this)
    }
    override fun onBind(intent: Intent): IBinder = binder
    override fun onStartCommand(i: Intent?, f: Int, s: Int): Int {
        if(!isServiceEnabled) { stopSelf(); return START_NOT_STICKY }
        return START_STICKY
    }
}
