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

class FPSOverlayService : Service() {
    companion object {
        var isRunning = false
        private var overlayView: View? = null
        private val handler = Handler(Looper.getMainLooper())
        private var fps = 0
        private var frameCount = 0
        private var lastTime = System.nanoTime()

        fun start(ctx: Context) {
            if (!isRunning) {
                val intent = Intent(ctx, FPSOverlayService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    ctx.startForegroundService(intent)
                } else {
                    ctx.startService(intent)
                }
            }
        }
        fun stop(ctx: Context) {
            if (isRunning) ctx.stopService(Intent(ctx, FPSOverlayService::class.java))
        }
    }

    private lateinit var windowManager: WindowManager
    private lateinit var fpsText: TextView

    override fun onCreate() {
        super.onCreate()
        windowManager = getSystemService(WINDOW_SERVICE) as WindowManager
        val layout = View.inflate(this, R.layout.overlay_fps, null)
        fpsText = layout.findViewById(R.id.fps_text)

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
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50
            y = 50
        }

        layout.setOnTouchListener { _, event ->
            if (event.action == MotionEvent.ACTION_DOWN) {
                params.x = event.rawX.toInt() - 100
                params.y = event.rawY.toInt() - 50
                windowManager.updateViewLayout(layout, params)
                true
            } else false
        }

        overlayView = layout
        windowManager.addView(layout, params)
        handler.post(object : Runnable {
            override fun run() {
                calculateFPS()
                handler.postDelayed(this, 500)
            }
        })
        isRunning = true
    }

    private fun calculateFPS() {
        frameCount++
        val now = System.nanoTime()
        if ((now - lastTime) / 1_000_000_000 >= 1) {
            fps = frameCount
            frameCount = 0
            lastTime = now
        }
        fpsText.text = "FPS $fps"
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        overlayView?.let { windowManager.removeView(it) }
        isRunning = false
    }
}
