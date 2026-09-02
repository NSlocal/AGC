package com.google.android.safetycore.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import com.google.android.safetycore.R

object NotificationHelper {
    const val CHANNEL_SERVICE = "safetycore_service"
    const val CHANNEL_WARNING = "safetycore_warning"
    const val CHANNEL_GAME = "safetycore_game"

    fun createChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channels = listOf(
                NotificationChannel(CHANNEL_SERVICE, "Layanan SafetyCore", NotificationManager.IMPORTANCE_LOW).apply {
                    description = "Status layanan pemindaian & overlay"
                },
                NotificationChannel(CHANNEL_WARNING, "Peringatan Konten", NotificationManager.IMPORTANCE_HIGH).apply {
                    description = "Peringatan konten sensitif atau berbahaya"
                },
                NotificationChannel(CHANNEL_GAME, "Game Booster", NotificationManager.IMPORTANCE_DEFAULT).apply {
                    description = "Notifikasi optimasi game & FPS"
                }
            )
            context.getSystemService(NotificationManager::class.java).apply {
                createNotificationChannels(channels)
            }
        }
    }

    fun showContentWarning(context: Context, title: String, message: String) {
        val intent = Intent(context, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            context, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_WARNING)
            .setSmallIcon(R.drawable.ic_scan)
            .setContentTitle("⚠️ $title")
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(1001, notification)
    }

    fun showGameBoostActive(context: Context, gameName: String, fps: Int) {
        val notification = NotificationCompat.Builder(context, CHANNEL_GAME)
            .setSmallIcon(R.drawable.ic_game)
            .setContentTitle("🎮 Game Booster Aktif")
            .setContentText("$gameName — $fps FPS")
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()

        context.getSystemService(NotificationManager::class.java)
            .notify(2001, notification)
    }

    fun cancelGameBoostNotification(context: Context) {
        context.getSystemService(NotificationManager::class.java).cancel(2001)
    }
}
