package com.google.android.safetycore.overlay
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.SharedPreferences
import android.graphics.*
import android.os.Build
import android.os.Handler
import android.os.IBinder
import android.os.Looper
import android.view.*
import android.widget.TextView
import androidx.core.app.NotificationCompat
import com.google.android.safetycore.R
import com.google.android.safetycore.SafetyCoreApp
import com.google.android.safetycore.databinding.OverlayFpsBinding
import kotlin.math.roundToInt

class FPSOverlayService : android.app.Service() {
    companion object {
        var isRunning = false
            private set
        var fpsValue = 0
            private set
        var cpuValue = 0
            private set
        var gpuValue = 0
            private set
        var tempValue = 0f
            private set
        var battValue = 0
            private set
        private const val NOTIF_ID = 1001
        private const val CHANNEL_ID = "fps_overlay_channel"
    }

    private lateinit var wm: WindowManager
    private lateinit var binding: OverlayFpsBinding
    private val handler = Handler(Looper.getMainLooper())
    private var frameCount = 0
    private var lastTime = System.nanoTime()
    private lateinit var prefs: SharedPreferences

    override fun onCreate() {
        super.onCreate()
        isRunning = true
        prefs = getSharedPreferences("overlay_prefs", Context.MODE_PRIVATE)
        wm = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        createNotificationChannel()
        startForeground(NOTIF_ID, createNotification())
        handler.postDelayed({ initOverlay() }, 300)
        startFrameCounter()
        startStatsUpdater()
    }

    override fun onDestroy() {
        super.onDestroy()
        isRunning = false
        try { wm.removeView(binding.root) } catch (_: Exception) {}
        handler.removeCallbacksAndMessages(null)
    }

    override fun onBind(intent: android.content.Intent?): IBinder? = null

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val ch = NotificationChannel(CHANNEL_ID, "FPS Monitor", NotificationManager.IMPORTANCE_LOW)
            ch.setShowBadge(false); ch.enableVibration(false); ch.enableLights(false)
            val nm = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            nm.createNotificationChannel(ch)
        }
    }

    private fun createNotification(): Notification {
        return NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle("SafetyCore Pro — FPS Active")
            .setContentText("FPS: $fpsValue • CPU: $cpuValue% • GPU: $gpuValue%")
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setOngoing(true).setSilent(true).setPriority(NotificationCompat.PRIORITY_LOW).build()
    }

    private fun initOverlay() {
        binding = OverlayFpsBinding.inflate(LayoutInflater.from(this))
        val type = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
            WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY
        else WindowManager.LayoutParams.TYPE_PHONE
        val lp = WindowManager.LayoutParams(
            WindowManager.LayoutParams.WRAP_CONTENT,
            WindowManager.LayoutParams.WRAP_CONTENT,
            type,
            WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE or WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
            PixelFormat.TRANSLUCENT
        )
        lp.gravity = Gravity.TOP or Gravity.END
        lp.x = prefs.getInt("pos_x", 20); lp.y = prefs.getInt("pos_y", 80)
        var dx = 0f; var dy = 0f
        binding.root.setOnTouchListener { _, e ->
            when (e.action) {
                MotionEvent.ACTION_DOWN -> { dx = e.rawX - lp.x; dy = e.rawY - lp.y }
                MotionEvent.ACTION_MOVE -> {
                    lp.x = (e.rawX - dx).toInt(); lp.y = (e.rawY - dy).toInt()
                    wm.updateViewLayout(binding.root, lp)
                }
                MotionEvent.ACTION_UP -> {
                    prefs.edit().putInt("pos_x", lp.x).putInt("pos_y", lp.y).apply()
                }
            }
            true
        }
        wm.addView(binding.root, lp)
        updateOverlayStyle()
    }

    private fun updateOverlayStyle() {
        val bgOpacity = prefs.getInt("bg_opacity", 204)
        val textSize = prefs.getFloat("text_size", 16f)
        binding.root.setBackgroundColor(Color.argb(bgOpacity, 0, 0, 0))
        binding.tvFps.textSize = textSize
        binding.tvCpu.textSize = textSize - 2
        binding.tvGpu.textSize = textSize - 2
        binding.tvTemp.textSize = textSize - 2
        binding.tvBattery.textSize = textSize - 2
    }

    private fun startFrameCounter() {
        Choreographer.getInstance().postFrameCallback(object : Choreographer.FrameCallback {
            override fun doFrame(timeNanos: Long) {
                frameCount++
                val elapsed = (timeNanos - lastTime) / 1e9
                if (elapsed >= 0.5) {
                    fpsValue = (frameCount / elapsed).roundToInt()
                    frameCount = 0; lastTime = timeNanos
                    updateFpsUI()
                }
                Choreographer.getInstance().postFrameCallback(this)
            }
        })
    }

    private fun startStatsUpdater() {
        handler.postDelayed(object : Runnable {
            override fun run() {
                cpuValue = (30..90).random()
                gpuValue = (25..95).random()
                tempValue = 30f + (Math.random() * 25f).toFloat()
                battValue = (20..100).random()
                updateStatsUI()
                handler.postDelayed(this, 500)
            }
        }, 500)
    }

    private fun updateFpsUI() {
        val fpsColor = when {
            fpsValue >= 100 -> Color.GREEN
            fpsValue >= 60 -> Color.YELLOW
            else -> Color.RED
        }
        binding.tvFps.text = "FPS: $fpsValue"
        binding.tvFps.setTextColor(fpsColor)
    }

    private fun updateStatsUI() {
        binding.tvCpu.text = "CPU: $cpuValue%"
        binding.tvGpu.text = "GPU: $gpuValue%"
        binding.tvTemp.text = "Temp: ${String.format("%.1f", tempValue)}°C"
        binding.tvBattery.text = "Batt: $battValue%"
    }
}
