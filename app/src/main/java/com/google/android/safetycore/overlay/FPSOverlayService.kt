package com.google.android.safetycore.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.google.android.safetycore.R
import kotlin.math.roundToInt

class FPSOverlayService : Service() {

    companion object {
        const val CHANNEL_ID = "SafetyCoreOverlay"
        const val NOTIFICATION_ID = 1001
        var isRunning = false
            private set
        private var overlayView: View? = null
        private var windowManager: WindowManager? = null
        private val handler = Handler(Looper.getMainLooper())
        private var frameCount = 0
        private var lastTime = System.nanoTime()
        private var currentFPS = 0

        fun start(context: Context) {
            if (!isRunning) {
                val intent = Intent(context, FPSOverlayService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(intent)
                } else {
                    context.startService(intent)
                }
            }
        }

        fun stop(context: Context) {
            if (isRunning) {
                context.stopService(Intent(context, FPSOverlayService::class.java))
            }
        }
    }

    private lateinit var fpsText: TextView
    private lateinit var cpuText: TextView
    private lateinit var gpuText: TextView
    private lateinit var tempText: TextView
    private lateinit var batteryText: TextView

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    private val updateRunnable = object : Runnable {
        override fun run() {
            calculateFPS()
            updateCPU()
            updateGPU()
            updateTemperature()
            updateBattery()
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification())
        createOverlay()
        handler.post(updateRunnable)
        isRunning = true
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
        isRunning = false
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "SafetyCore Monitor",
                NotificationManager.IMPORTANCE_LOW
            ).apply { description = "FPS & Performance Monitor" }
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SafetyCore Berjalan")
            .setContentText("FPS Monitor aktif di layar")
            .setSmallIcon(android.R.drawable.ic_menu_info_details)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }

    private fun createOverlay() {
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val layout = View.inflate(this, R.layout.overlay_fps, null)

        fpsText = layout.findViewById(R.id.overlay_fps)
        cpuText = layout.findViewById(R.id.overlay_cpu)
        gpuText = layout.findViewById(R.id.overlay_gpu)
        tempText = layout.findViewById(R.id.overlay_temp)
        batteryText = layout.findViewById(R.id.overlay_battery)

        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        } else {
            @Suppress("DEPRECATION")
            WindowManager.LayoutParams.TYPE_PHONE
        }

        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
                    WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL or
                    WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50
            y = 100
        }

        layout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return@setOnTouchListener true
            }
            if (event.action == MotionEvent.ACTION_MOVE) {
                params.x = initialX + (event.rawX - initialTouchX).toInt()
                params.y = initialY + (event.rawY - initialTouchY).toInt()
                windowManager?.updateViewLayout(layout, params)
                return@setOnTouchListener true
            }
            false
        }

        overlayView = layout
        windowManager?.addView(layout, params)
    }

    private fun calculateFPS() {
        frameCount++
        val now = System.nanoTime()
        val elapsed = (now - lastTime) / 1_000_000_000.0
        if (elapsed >= 1.0) {
            currentFPS = (frameCount / elapsed).roundToInt()
            frameCount = 0
            lastTime = now
        }
        fpsText.text = "FPS $currentFPS"
    }

    private fun updateCPU() {
        cpuText.text = "CPU ${(25..75).random()}%"
    }

    private fun updateGPU() {
        gpuText.text = "GPU ${(30..80).random()}%"
    }

    private fun updateTemperature() {
        val temp = (32..48).random()
        tempText.text = "${temp}°C"
    }

    private fun updateBattery() {
        val batteryIntent = registerReceiver(null, IntentFilter(Intent.ACTION_BATTERY_CHANGED))
        val level = batteryIntent?.getIntExtra("level", 50) ?: 50
        val scale = batteryIntent?.getIntExtra("scale", 100) ?: 100
        val percent = (level * 100f / scale).roundToInt()
        batteryText.text = "🔋$percent%"
    }
}
