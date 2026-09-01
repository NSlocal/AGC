package com.google.android.safetycore.content
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import com.google.android.safetycore.R
import com.google.android.safetycore.ui.SettingsActivity

class ContentScannerService : android.app.Service() {
    companion object {
        const val TAG = "ContentScannerService"
        const val CHANNEL_ID = "SafetyCoreScanner"
        const val EXTRA_URI = "image_uri"
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(1, buildNotification())
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!SettingsActivity.isScanEnabled(this)) {
            Log.d(TAG, "Pemindaian dinonaktifkan — berhenti")
            stopSelf()
            return START_NOT_STICKY
        }
        intent?.getParcelableExtra<Uri>(EXTRA_URI)?.let { scanImage(it) }
        return START_NOT_STICKY
    }

    private fun scanImage(uri: Uri) {
        try {
            contentResolver.openInputStream(uri)?.use { stream ->
                val bitmap = BitmapFactory.decodeStream(stream)
                bitmap?.let {
                    val result = ContentClassifier.classify(it)
                    handleResult(result, uri)
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Scan gagal", e)
        }
        stopSelf()
    }

    private fun handleResult(result: ScanResult, uri: Uri) {
        when (result.status) {
            ScanStatus.ALLOWED -> Log.d(TAG, "Diizinkan")
            ScanStatus.WARNING -> if (SettingsActivity.isAutoBlurEnabled(this)) {
                startService(Intent(this, com.google.android.safetycore.overlay.BlurOverlayService::class.java))
            }
            ScanStatus.BLOCKED -> if (SettingsActivity.isBlockEnabled(this)) {
                Log.e(TAG, "DIBLOKIR: ${result.reason}")
            }
            ScanStatus.UNCLASSIFIED -> Log.d(TAG, "Tidak diklasifikasikan")
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SafetyCore Berjalan")
            .setContentText("Pemindaian konten aktif")
            .setSmallIcon(R.drawable.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .build()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel(CHANNEL_ID, "SafetyCore Scanner", NotificationManager.IMPORTANCE_LOW).also {
                getSystemService(NotificationManager::class.java).createNotificationChannel(it)
            }
        }
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
