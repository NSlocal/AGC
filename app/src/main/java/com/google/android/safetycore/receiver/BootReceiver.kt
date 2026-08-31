package com.google.android.safetycore.receiver
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.preference.PreferenceManager
import com.google.android.safetycore.overlay.FPSOverlayService
import com.google.android.safetycore.service.GameBoostService

class BootReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (intent?.action == Intent.ACTION_BOOT_COMPLETED && context != null) {
            val prefs = PreferenceManager.getDefaultSharedPreferences(context)
            if (prefs.getBoolean("auto_start_fps", false)) {
                context.startService(Intent(context, FPSOverlayService::class.java))
            }
            if (prefs.getBoolean("auto_start_boost", false)) {
                context.startService(Intent(context, GameBoostService::class.java))
            }
        }
    }
}
