package com.google.android.safetycore.overlay

import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.Gravity
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import com.google.android.safetycore.databinding.OverlayDraggableFpsBinding
import com.google.android.safetycore.ui.SettingsActivity

class DraggableFpsOverlay : android.app.Service(), View.OnTouchListener {
    private var overlayView: View? = null
    private lateinit var windowManager: WindowManager
    private lateinit var binding: OverlayDraggableFpsBinding
    
    private var initialX = 0
    private var initialY = 0
    private var initialTouchX = 0f
    private var initialTouchY = 0f
    
    private val handler = Handler(Looper.getMainLooper())
    private var frameCount = 0
    private var lastTime = System.currentTimeMillis()

    private val updateRunnable = object : Runnable {
        override fun run() {
            if (!SettingsActivity.isFPSOverlayEnabled(this@DraggableFpsOverlay)) {
                stopSelf()
                return
            }
            val now = System.currentTimeMillis()
            val fps = (frameCount * 1000f / (now - lastTime)).toInt()
            frameCount = 0
            lastTime = now
            
            binding.tvFps.text = "FPS: $fps"
            
            // Warna otomatis berdasarkan FPS
            binding.tvFps.setTextColor(when {
                fps >= 110 -> 0xFF4CAF50.toInt() // Hijau - sangat bagus
                fps >= 60 -> 0xFFFFC107.toInt()  // Kuning - bagus
                else -> 0xFFF44336.toInt()       // Merah - rendah
            })
            
            frameCount++
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate() {
        super.onCreate()
        if (!SettingsActivity.isFPSOverlayEnabled(this)) {
            stopSelf()
            return
        }
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        showOverlay()
        handler.post(updateRunnable)
    }

    private fun showOverlay() {
        binding = OverlayDraggableFpsBinding.inflate(layoutInflater)
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
        ).apply {
            gravity = Gravity.TOP or Gravity.START
            x = 50
            y = 100
        }

        windowManager.addView(overlayView, params)
        binding.root.setOnTouchListener(this)
    }

    override fun onTouch(v: View?, event: MotionEvent?): Boolean {
        val params = overlayView?.layoutParams as WindowManager.LayoutParams
        when (event?.action) {
            MotionEvent.ACTION_DOWN -> {
                initialX = params.x
                initialY = params.y
                initialTouchX = event.rawX
                initialTouchY = event.rawY
                return true
            }
            MotionEvent.ACTION_MOVE -> {
                params.x = initialX + (event.rawX - initialTouchX).toInt()
                params.y = initialY + (event.rawY - initialTouchY).toInt()
                windowManager.updateViewLayout(overlayView, params)
                return true
            }
        }
        return false
    }

    override fun onDestroy() {
        handler.removeCallbacks(updateRunnable)
        overlayView?.let { windowManager.removeView(it) }
        super.onDestroy()
    }

    override fun onBind(intent: android.content.Intent?): IBinder? = null
}
