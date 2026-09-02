package com.google.android.safetycore.ui

import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.R
import com.google.android.safetycore.databinding.ActivityDeviceInfoBinding
import com.google.android.safetycore.monitor.SystemMonitor

class DeviceInfoActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDeviceInfoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDeviceInfoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvDeviceModel.text = Build.MODEL
        binding.tvManufacturer.text = Build.MANUFACTURER
        binding.tvAndroidVersion.text = Build.VERSION.RELEASE
        binding.tvSdkLevel.text = Build.VERSION.SDK_INT.toString()
        binding.tvCpu.text = Build.HARDWARE

        val isScanRunning = SettingsActivity.isScanEnabled(this)
        val isFpsRunning = SettingsActivity.isFPSOverlayEnabled(this)
        val isMonitorRunning = SettingsActivity.isSystemMonitorEnabled(this)

        binding.tvScanStatus.text = if (isScanRunning) "✅ Aktif" else "❌ Dinonaktifkan"
        binding.tvFpsStatus.text = if (isFpsRunning) "✅ Aktif" else "❌ Dinonaktifkan"
        binding.tvMonitorStatus.text = if (isMonitorRunning) "✅ Aktif" else "❌ Dinonaktifkan"

        binding.btnRefresh.setOnClickListener {
            Toast.makeText(this, "Diperbarui", Toast.LENGTH_SHORT).show()
            recreate()
        }
    }
}
