package com.google.android.safetycore.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.overlay.FPSOverlayService
import com.google.android.safetycore.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySettingsBinding
    private val OVERLAY_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupClickListeners()
        updateServiceToggleState()
    }

    override fun onResume() {
        super.onResume()
        updateServiceToggleState()
    }

    private fun setupClickListeners() {
        binding.btnToggleFPS.setOnClickListener {
            if (checkOverlayPermission()) {
                toggleOverlayService()
            } else {
                requestOverlayPermission()
            }
        }
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else {
            true
        }
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_CODE)
        }
    }

    private fun toggleOverlayService() {
        val serviceRunning = FPSOverlayService.isRunning.value
        val intent = Intent(this, FPSOverlayService::class.java)

        if (serviceRunning) {
            stopService(intent)
            Toast.makeText(this, "FPS Overlay Stopped", Toast.LENGTH_SHORT).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            Toast.makeText(this, "FPS Overlay Started", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateServiceToggleState() {
        val isRunning = FPSOverlayService.isRunning.value
        binding.btnToggleFPS.text = if (isRunning) "STOP FPS MONITOR" else "SHOW FPS MONITOR"
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_CODE) {
            if (checkOverlayPermission()) {
                toggleOverlayService()
            } else {
                Toast.makeText(this, "Overlay Permission Required", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
