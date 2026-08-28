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
        private const val NOTIF_ID = 1001
        private const val CHANNEL_ID = "fps_channel"
    }

    private lateinit var wm: WindowManager
    private var view: View? = null
    private val handler = Handler(Looper.getMainLooper())
    private var fpsTv: TextView? = null
    private var frameCount = 0
    private var lastTime = System.nanoTime()

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        
        createChannel()
        startForeground(NOTIF_ID, createNotif())
        
        handler.postDelayed({ initOverlay() }, 200)
        startFpsCounter()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        try { view?.let { wm.removeView(it) } } catch (_: Exception) {}
        view = null
    }

    override fun onBind(intent: android.content.Intent?): IBinder? = null

    private fun createChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, "FPS Monitor", NotificationManager.IMPORTANCE_LOW)
            ch.setShowBadge(false)
            ch.enableVibration(false)
            ch.enableLights(false)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(ch)
        }
    }

    private fun createNotif(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SafetyCore Pro")
            .setContentText("FPS Running")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true)
            .setSilent(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()
    }

    private fun initOverlay() {
        try {
            val inf = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            view = inf.inflate(R.layout.overlay_fps, null)
            fpsTv = view?.findViewById(R.id.tv_fps)

            val lp = WindowManager.LayoutParams(
                WindowManager.LayoutParams.WRAP_CONTENT,
                WindowManager.LayoutParams.WRAP_CONTENT,
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
                else WindowManager.LayoutParams.TYPE_PHONE,
                WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                android.graphics.PixelFormat.TRANSLUCENT
            )
            lp.gravity = Gravity.TOP or Gravity.END
            lp.x = 20
            lp.y = 80

            var dx = 0f
            var dy = 0f
            view?.setOnTouchListener { _, e ->
                when (e.action) {
                    MotionEvent.ACTION_DOWN -> {
                        dx = e.rawX - lp.x
                        dy = e.rawY - lp.y
                    }
                    MotionEvent.ACTION_MOVE -> {
                        lp.x = (e.rawX - dx).toInt()
                        lp.y = (e.rawY - dy).toInt()
                        wm.updateViewLayout(view, lp)
                    }
                }
                true
            }

            wm.addView(view, lp)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startFpsCounter() {
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(timeNanos: Long) {
                frameCount++
                val sec = (timeNanos - lastTime) / 1e9
                if (sec >= 0.5) {
                    val fps = (frameCount / sec).roundToInt()
                    fpsTv?.text = "FPS: $fps"
                    frameCount = 0
                    lastTime = timeNanos
                }
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }
}
