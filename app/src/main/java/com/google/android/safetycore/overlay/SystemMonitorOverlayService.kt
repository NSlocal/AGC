package com.google.android.safetycore.overlay

import android.app.Service
import android.content.Context
import android.graphics.PixelFormat
import android.os.Build
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.WindowManager
import com.google.android.safetycore.R
import com.google.android.safetycore.databinding.OverlaySystemMonitorBinding
import com.google.android.safetycore.monitor.SystemMonitor
import com.google.android.safetycore.ui.SettingsActivity

class SystemMonitorOverlayService : Service() {
    private var overlayView: View? = null
    private lateinit var windowManager: WindowManager

    override fun onCreate() {
        super.onCreate()
        if (!SettingsActivity.isSystemMonitorEnabled(this)) {
            stopSelf()
            return
        }
        windowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        showOverlay()
        SystemMonitor.start()
        SystemMonitor.addListener { cpu, gpu, temp, battTemp ->
            overlayView?.let { view ->
                val binding = OverlaySystemMonitorBinding.bind(view)
                binding.tvCpu.text = "CPU: ${cpu.toInt()}%"
                binding.tvGpu.text = "GPU: ${gpu.toInt()}%"
                binding.tvTemp.text = "Temp: ${temp.toInt()}°C"
                binding.tvBatteryTemp.text = "Batt: ${battTemp.toInt()}°C"

                binding.tvCpu.setTextColor(
                    if (cpu < 70) 0xFF4CAF50.toInt()
                    else if (cpu < 90) 0xFFFFC107.toInt()
                    else 0xFFF44336.toInt()
                )
                binding.tvTemp.setTextColor(
                    if (temp < 45) 0xFF4CAF50.toInt()
                    else if (temp < 55) 0xFFFFC107.toInt()
                    else 0xFFF44336.toInt()
                )
            }
        }
    }

    private fun showOverlay() {
        val binding = OverlaySystemMonitorBinding.inflate(LayoutInflater.from(this))
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
        ).apply { gravity = Gravity.TOP or Gravity.END; x = 20; y = 20 }
        windowManager.addView(overlayView, params)
    }

    override fun onDestroy() {
        SystemMonitor.stop()
        overlayView?.let { windowManager.removeView(it) }
        super.onDestroy()
    }

    override fun onBind(intent: android.content.Intent?): IBinder? = null
}
