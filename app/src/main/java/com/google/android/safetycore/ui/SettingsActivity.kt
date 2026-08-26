package com.google.android.safetycore.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Switch
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.R
import com.google.android.safetycore.SafetyCoreService
import com.google.android.safetycore.overlay.FPSOverlayService

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchGlobal: Switch
    private lateinit var btnOverlay: Button
    private lateinit var btnGameBooster: Button
    private val REQUEST_OVERLAY = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safetycore_settings)

        switchGlobal = findViewById(R.id.switch_global_enable)
        btnOverlay = findViewById(R.id.btn_toggle_overlay)
        btnGameBooster = findViewById(R.id.btn_go_game_booster)

        switchGlobal.isChecked = SafetyCoreService.isEnabled(this)
        updateOverlayButton()

        switchGlobal.setOnCheckedChangeListener { _, isChecked ->
            SafetyCoreService.setEnabled(this, isChecked)
            Toast.makeText(this, if (isChecked) "✅ SafetyCore Aktif" else "❌ Dinonaktifkan", Toast.LENGTH_SHORT).show()
        }

        btnOverlay.setOnClickListener {
            if (FPSOverlayService.isRunning) {
                FPSOverlayService.stop(this)
                Toast.makeText(this, "✅ Overlay Dimatikan", Toast.LENGTH_SHORT).show()
            } else {
                if (checkOverlayPermission()) {
                    FPSOverlayService.start(this)
                    Toast.makeText(this, "✅ Overlay Dinyalakan", Toast.LENGTH_SHORT).show()
                } else {
                    requestOverlayPermission()
                }
            }
            updateOverlayButton()
        }

        btnGameBooster.setOnClickListener {
            startActivity(Intent(this, GameBoosterActivity::class.java))
        }
    }

    private fun updateOverlayButton() {
        if (FPSOverlayService.isRunning) {
            btnOverlay.text = "🔴 MATIKAN FPS MONITOR"
            btnOverlay.setBackgroundColor(0xFFFF5722.toInt())
        } else {
            btnOverlay.text = "🟢 TAMPILKAN FPS MONITOR"
            btnOverlay.setBackgroundColor(0xFFFF9800.toInt())
        }
    }

    private fun checkOverlayPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else true
    }

    private fun requestOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, REQUEST_OVERLAY)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_OVERLAY) {
            if (checkOverlayPermission()) {
                FPSOverlayService.start(this)
                Toast.makeText(this, "✅ Izin diberikan! Overlay aktif", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "⚠️ Izin ditolak", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateOverlayButton()
    }
}
