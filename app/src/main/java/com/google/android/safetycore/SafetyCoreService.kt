package com.google.android.safetycore

import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.IBinder
import android.util.Log

class SafetyCoreService : Service() {

    companion object {
        private const val TAG = "SafetyCoreService"
        private const val PREFS_NAME = "safety_core_prefs"
        private const val KEY_IS_ENABLED = "is_enabled"

        var isServiceEnabled: Boolean = true
            private set

        fun setEnabled(context: Context, enabled: Boolean) {
            isServiceEnabled = enabled
            context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .edit().putBoolean(KEY_IS_ENABLED, enabled).apply()
            Log.d(TAG, "SafetyCore ${if (enabled) "✅ DIAKTIFKAN" else "❌ DINONAKTIFKAN"}")
        }

        fun isEnabled(context: Context): Boolean {
            return context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
                .getBoolean(KEY_IS_ENABLED, true)
        }
    }

    private val binder = object : ISafetyCoreService.Stub() {
        override fun isEnabled(): Boolean = isServiceEnabled
        override fun setFeatureEnabled(featureId: String, enabled: Boolean) {
            when (featureId) {
                "scan_images" -> ContentClassifier.isScanningEnabled = enabled
                "warn_nudity" -> ContentClassifier.nudityWarningEnabled = enabled
                "warn_sensitive" -> ContentClassifier.sensitiveWarningEnabled = enabled
                "blur_content" -> ContentClassifier.blurEnabled = enabled
            }
            Log.d(TAG, "Fitur $featureId → ${if (enabled) "ON" else "OFF"}")
        }
        override fun getFeatureStatus(featureId: String): Boolean {
            return when (featureId) {
                "scan_images" -> ContentClassifier.isScanningEnabled
                "warn_nudity" -> ContentClassifier.nudityWarningEnabled
                "warn_sensitive" -> ContentClassifier.sensitiveWarningEnabled
                "blur_content" -> ContentClassifier.blurEnabled
                else -> false
            }
        }
    }

    override fun onCreate() {
        super.onCreate()
        isServiceEnabled = isEnabled(this)
        Log.d(TAG, "Layanan dibuat | Status: ${if (isServiceEnabled) "AKTIF" else "DINONAKTIF"}")
    }

    override fun onBind(intent: Intent): IBinder = binder

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isServiceEnabled) {
            Log.i(TAG, "⚠️ SafetyCore dinonaktifkan — hentikan pemrosesan")
            stopSelf()
            return START_NOT_STICKY
        }
        return START_STICKY
    }
}
