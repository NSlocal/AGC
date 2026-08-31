package com.google.android.safetycore.manager

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.safetycore.R

class AppNotificationManager(private val context: Context) {
    private val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    companion object {
        const val CHANNEL_FPS = "fps_overlay_channel"
        const val CHANNEL_BOOST = "game_boost_channel"
        const val CHANNEL_ALERT = "fps_alert_channel"
    }

    fun createAllChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            listOf(
                NotificationChannel(CHANNEL_FPS, "FPS Monitor", NotificationManager.IMPORTANCE_LOW),
                NotificationChannel(CHANNEL_BOOST, "Game Booster", NotificationManager.IMPORTANCE_LOW),
                NotificationChannel(CHANNEL_ALERT, "FPS Alerts", NotificationManager.IMPORTANCE_DEFAULT)
            ).forEach {
                it.setShowBadge(false)
                it.enableVibration(false)
                nm.createNotificationChannel(it)
            }
        }
    }

    fun sendFpsDropAlert(currentFps: Int, threshold: Int) {
        val notif = NotificationCompat.Builder(context, CHANNEL_ALERT)
            .setContentTitle("⚠️ FPS Drop Detected")
            .setContentText("FPS dropped to $currentFps (below $threshold)")
            .setSmallIcon(android.R.drawable.stat_notify_warning)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .setAutoCancel(true)
            .build()
        nm.notify(3001, notif)
    }

    fun updateFpsNotification(fps: Int, cpu: Int, gpu: Int) {
        val notif = NotificationCompat.Builder(context, CHANNEL_FPS)
            .setContentTitle("SafetyCore Pro — FPS Active")
            .setContentText("FPS: $fps • CPU: $cpu% • GPU: $gpu%")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setSilent(true)
            .build()
        nm.notify(1001, notif)
    }
}
