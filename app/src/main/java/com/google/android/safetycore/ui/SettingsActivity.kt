package com.google.android.safetycore.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.overlay.FPSOverlayService
import com.google.android.safetycore.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnToggleFPS: Button
    private val OVERLAY_PERMISSION_CODE = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnToggleFPS = findViewById(R.id.btn_toggle_fps)
        updateButton()

        btnToggleFPS.setOnClickListener {
            if (checkPermission()) {
                toggleService()
            } else {
                requestPermission()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateButton()
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else true
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_CODE)
        }
    }

    private fun toggleService() {
        val intent = Intent(this, FPSOverlayService::class.java)
        if (FPSOverlayService.isRunning) {
            stopService(intent)
            Toast.makeText(this, "FPS Monitor Stopped", Toast.LENGTH_SHORT).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            Toast.makeText(this, "FPS Monitor Started — Drag to move!", Toast.LENGTH_SHORT).show()
        }
        updateButton()
    }

    private fun updateButton() {
        btnToggleFPS.text = if (FPSOverlayService.isRunning) {
            "STOP FPS MONITOR"
        } else {
            "SHOW FPS MONITOR"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_CODE && checkPermission()) {
            toggleService()
        }
    }
}
