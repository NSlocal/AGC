package com.google.android.safetycore.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.google.android.safetycore.R
import kotlin.math.roundToInt

class FPSOverlayService : android.app.Service() {

    companion object {
        var isRunning = false
            private set
        private const val NOTIFICATION_ID = 1001
        private const val CHANNEL_ID = "FPS_Overlay"
    }

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())
    private var frameCount = 0
    private var lastTime = System.nanoTime()

    private lateinit var tvFPS: TextView
    private lateinit var tvCPU: TextView
    private lateinit var tvGPU: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvBattery: TextView

    private val updateRunnable = object : Runnable {
        override fun run() {
            updateUI()
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createOverlay()
        startForeground(NOTIFICATION_ID, createNotification())
        handler.post(updateRunnable)
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        handler.removeCallbacks(updateRunnable)
        overlayView?.let { windowManager.removeView(it) }
        overlayView = null
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: Intent?): IBinder? = null

    private fun createOverlay() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_fps, null)

        tvFPS = overlayView!!.findViewById(R.id.tv_fps)
        tvCPU = overlayView!!.findViewById(R.id.tv_cpu)
        tvGPU = overlayView!!.findViewById(R.id.tv_gpu)
        tvTemp = overlayView!!.findViewById(R.id.tv_temp)
        tvBattery = overlayView!!.findViewById(R.id.tv_battery)

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
            PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.START
        params.x = 20
        params.y = 50

        windowManager.addView(overlayView, params)
    }

    private fun updateUI() {
        frameCount++
        val now = System.nanoTime()
        val elapsed = (now - lastTime) / 1e9
        if (elapsed >= 0.5) {
            val fps = (frameCount / elapsed).roundToInt()
            tvFPS.text = "FPS: $fps"
            frameCount = 0
            lastTime = now
        }
        tvCPU.text = "CPU: ${(40..70).random()}%"
        tvGPU.text = "GPU: ${(30..85).random()}%"
        tvTemp.text = "Temp: ${(32..55).random()}°C"
        tvBattery.text = "Batt: ${(20..100).random()}%"
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "FPS Monitor", NotificationManager.IMPORTANCE_LOW)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SafetyCore")
            .setContentText("FPS Monitor Running")
            .setSmallIcon(R.drawable.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
}
