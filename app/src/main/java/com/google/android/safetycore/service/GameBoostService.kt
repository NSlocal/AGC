package com.google.android.safetycore.service
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import androidx.core.app.NotificationCompat
import com.google.android.safetycore.R

class GameBoostService : android.app.Service() {
    companion object {
        var isRunning = false
            private set
        private const val NOTIF_ID = 2002
        private const val CHANNEL_ID = "game_boost_channel"
        val BOOSTED_PACKAGES = listOf(
            "com.tencent.tmgp.speedmobile",
            "com.garena.game.fctw",
            "com.miHoYo.GenshinImpact",
            "com.riotgames.leagueoflegends.wildrift"
        )
    }

    private val handler = Handler(Looper.getMainLooper())

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        createChannel()
        startForeground(NOTIF_ID, buildNotif())
        handler.postDelayed({ applyBoost() }, 500)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacksAndMessages(null)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, "Game Booster", NotificationManager.IMPORTANCE_LOW)
            ch.setShowBadge(false)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(ch)
        }
    }

    private fun buildNotif(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("🚀 Game Booster Active")
            .setContentText("Optimization running for supported games")
            .setSmallIcon(android.R.drawable.ic_menu_myplaces)
            .setOngoing(true).setSilent(true).build()
    }

    private fun applyBoost() {
        // Non-root optimizations
        try {
            // Set refresh rate hint
            // Performance mode hints
        } catch (e: Exception) { e.printStackTrace() }
    }
}
