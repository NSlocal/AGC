package com.google.android.safetycore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.R
import com.google.android.safetycore.monitor.SystemMonitor
import com.google.android.safetycore.service.SystemMonitorOverlayService
import kotlinx.android.synthetic.main.activity_device_info.*

class DeviceInfoActivity : AppCompatActivity() {
    private var systemMonitor: SystemMonitor? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_device_info)

        if (SystemMonitorOverlayService.isSystemMonitorEnabled(this)) {
            systemMonitor = SystemMonitor(this)
            tvCpu.text = "CPU: Aktif"
        } else {
            tvCpu.text = "CPU: Tidak dipantau"
        }
    }
}
