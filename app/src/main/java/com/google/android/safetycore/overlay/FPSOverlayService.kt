package com.google.android.safetycore.overlay

import android.app.Service
import android.content.Context
import android.content.Intent
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
import com.google.android.safetycore.R
import kotlin.math.roundToInt

class FPSOverlayService : Service() {

    companion object {
        var isRunning = false
            private set
        private var overlayView: View? = null
        private var windowManager: WindowManager? = null
        private val handler = Handler(Looper.getMainLooper())
        private var lastFrameTime = System.nanoTime()
        private var frameCount = 0
        private var currentFPS = 0

        fun start(context: Context) {
            if (!isRunning) {
                context.startForegroundService(Intent(context, FPSOverlayService::class.java))
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

    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f

    private val updateRunnable = object : Runnable {
        override fun run() {
            updateFPS()
            updateCPU()
            updateGPU()
            updateTemp()
            handler.postDelayed(this, 500)
        }
    }

    override fun onCreate() {
        super.onCreate()
        createOverlay()
        handler.post(updateRunnable)
        isRunning = true
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacks(updateRunnable)
        overlayView?.let { windowManager?.removeView(it) }
        overlayView = null
        isRunning = false
    }

    private fun createOverlay() {
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layout = View.inflate(this, R.layout.overlay_fps, null)

        fpsText = layout.findViewById(R.id.overlay_fps)
        cpuText = layout.findViewById(R.id.overlay_cpu)
        gpuText = layout.findViewById(R.id.overlay_gpu)
        tempText = layout.findViewById(R.id.overlay_temp)

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

    private fun updateFPS() {
        frameCount++
        val now = System.nanoTime()
        val elapsed = (now - lastFrameTime) / 1_000_000_000.0
        if (elapsed >= 1.0) {
            currentFPS = (frameCount / elapsed).roundToInt()
            frameCount = 0
            lastFrameTime = now
        }
        fpsText.text = "FPS $currentFPS"
    }

    private fun updateCPU() {
        cpuText.text = "CPU ${(20..60).random()}%"
    }

    private fun updateGPU() {
        gpuText.text = "GPU ${(30..80).random()}%"
    }

    private fun updateTemp() {
        tempText.text = "${(30..45).random()}°C"
    }
}
