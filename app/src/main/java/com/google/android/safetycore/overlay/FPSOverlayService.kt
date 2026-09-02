package com.google.android.safetycore.overlay
import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.google.android.safetycore.R
import com.google.android.safetycore.databinding.OverlayFpsBinding
import com.google.android.safetycore.ui.SettingsActivity

class FPSOverlayService : Service() {
    companion object {
        var isRunning = false
    }

    private var overlayView: View? = null
    private lateinit var windowManager: WindowManager
    private val handler = Handler(Looper.getMainLooper())
    private var frameCount = 0
    private var lastTime = System.currentTimeMillis()

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (!SettingsActivity.isFPSOverlayEnabled(this@FPSOverlayService)) {
                stopSelf()
                return
            }
            val now = System.currentTimeMillis()
            val fps = (frameCount * 1000f / (now - lastTime)).toInt()
            frameCount = 0; lastTime = now
            overlayView?.let {
                OverlayFpsBinding.bind(it).tvFps.text = "FPS: $fps"
            }
            frameCount++
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (!SettingsActivity.isFPSOverlayEnabled(this)) { stopSelf(); return }
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        showOverlay()
        handler.post(updateRunnable)
    }

    private fun showOverlay() {
        val binding = OverlayFpsBinding.inflate(LayoutInflater.from(this))
        overlayView = binding.root
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE
        val params = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
            PixelFormat.TRANSLUCENT
        ).apply { gravity = Gravity.TOP or Gravity.START; x = 20; y = 20 }
        windowManager.addView(overlayView, params)
    }

    override fun onDestroy() {
        handler.removeCallbacks(updateRunnable)
        overlayView?.let { windowManager.removeView(it) }
        super.onDestroy()
    }

    override fun onBind(intent: android.content.Intent?): IBinder? = null
}
