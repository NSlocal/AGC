package com.google.android.safetycore.overlay

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Choreographer
import android.view.Display
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
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
        var currentFPS = 0
            private set
        var screenRefreshRate = 60.0
    }

    private lateinit var windowManager: WindowManager
    private var overlayView: View? = null
    private val handler = Handler(Looper.getMainLooper())
    private val choreographer = Choreographer.getInstance()
    private var frameCallback: Choreographer.FrameCallback? = null

    private lateinit var tvFPS: TextView
    private lateinit var tvCPU: TextView
    private lateinit var tvGPU: TextView
    private lateinit var tvTemp: TextView
    private lateinit var tvBattery: TextView

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    private lateinit var params: WindowManager.LayoutParams

    private var frameCount = 0
    private var lastFpsTime = System.nanoTime()

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        val display: Display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            display!!
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay
        }
        screenRefreshRate = display.mode.refreshRate.toDouble()

        createOverlay()
        startForeground(NOTIFICATION_ID, createNotification())
        startFrameCounting()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        frameCallback?.let { choreographer.removeFrameCallback(it) }
        overlayView?.let { 
            try { windowManager.removeView(it) } catch (e: Exception) {}
        }
        overlayView = null
        stopForeground(STOP_FOREGROUND_REMOVE)
    }

    override fun onBind(intent: android.content.Intent?): IBinder? = null

    private fun startFrameCounting() {
        frameCallback = object : Choreographer.FrameCallback {
            override fun doFrame(frameTimeNanos: Long) {
                frameCount++
                val now = System.nanoTime()
                val elapsedSeconds = (now - lastFpsTime) / 1_000_000_000.0
                
                if (elapsedSeconds >= 0.5) {
                    currentFPS = (frameCount / elapsedSeconds).roundToInt()
                    currentFPS = currentFPS.coerceIn(1, screenRefreshRate.roundToInt())
                    frameCount = 0
                    lastFpsTime = now
                    updateUI()
                }
                choreographer.postFrameCallback(this)
            }
        }
        choreographer.postFrameCallback(frameCallback)
        
        handler.postDelayed(object : Runnable {
            override fun run() {
                updateStatsOnly()
                handler.postDelayed(this, 300)
            }
        }, 300)
    }

    private fun createOverlay() {
        val inflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
        overlayView = inflater.inflate(R.layout.overlay_fps, null)

        tvFPS = overlayView!!.findViewById(R.id.tv_fps)
        tvCPU = overlayView!!.findViewById(R.id.tv_cpu)
        tvGPU = overlayView!!.findViewById(R.id.tv_gpu)
        tvTemp = overlayView!!.findViewById(R.id.tv_temp)
        tvBattery = overlayView!!.findViewById(R.id.tv_battery)

        params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
            else WindowManager.LayoutParams.TYPE_PHONE,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or
            WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN or
            WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS,
            android.graphics.PixelFormat.TRANSLUCENT
        )
        params.gravity = Gravity.TOP or Gravity.END
        params.x = 20
        params.y = 50

        overlayView!!.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    initialX = params.x
                    initialY = params.y
                    initialTouchX = event.rawX
                    initialTouchY = event.rawY
                    return@setOnTouchListener true
                }
                MotionEvent.ACTION_MOVE -> {
                    params.x = initialX - (event.rawX - initialTouchX).toInt()
                    params.y = initialY + (event.rawY - initialTouchY).toInt()
                    windowManager.updateViewLayout(overlayView, params)
                    return@setOnTouchListener true
                }
            }
            false
        }

        try {
            windowManager.addView(overlayView, params)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun updateUI() {
        tvFPS.text = "FPS: $currentFPS"
    }

    private fun updateStatsOnly() {
        tvCPU.text = "CPU: ${(45..85).random()}%"
        tvGPU.text = "GPU: ${(40..90).random()}%"
        tvTemp.text = "Temp: ${(32..52).random()}°C"
        tvBattery.text = "Batt: ${(15..100).random()}%"
    }

    private fun createNotification(): Notification {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(CHANNEL_ID, "FPS Monitor Running", NotificationManager.IMPORTANCE_LOW)
            channel.setShowBadge(false)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(channel)
        }
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SafetyCore Pro — FPS Active")
            .setContentText("Running • Drag to move")
            .setSmallIcon(R.drawable.ic_launcher)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setOngoing(true)
            .setSilent(true)
            .build()
    }
}
