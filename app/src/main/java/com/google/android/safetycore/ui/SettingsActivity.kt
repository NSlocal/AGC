package com.google.android.safetycore.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
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
    private lateinit var spinnerFPS: Spinner
    private val REQUEST_OVERLAY = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safetycore_settings)

        switchGlobal = findViewById(R.id.switch_global_enable)
        btnOverlay = findViewById(R.id.btn_toggle_overlay)
        btnGameBooster = findViewById(R.id.btn_go_game_booster)
        spinnerFPS = findViewById(R.id.spinner_fps)

        // FPS Selector
        val fpsOptions = listOf("60 FPS", "90 FPS", "120 FPS", "144 FPS")
        spinnerFPS.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, fpsOptions)
        spinnerFPS.setSelection(0)

        // Set status awal
        switchGlobal.isChecked = SafetyCoreService.isEnabled(this)
        updateOverlayButton()

        // SafetyCore Toggle
        switchGlobal.setOnCheckedChangeListener { _, isChecked ->
            SafetyCoreService.setEnabled(this, isChecked)
            Toast.makeText(this, if (isChecked) "✅ SafetyCore Aktif" else "❌ Dinonaktifkan", Toast.LENGTH_SHORT).show()
        }

        // FPS Selector Change
        spinnerFPS.onItemSelectedListener = object : android.widget.AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: android.widget.AdapterView<*>?, p1: android.view.View?, pos: Int, id: Long) {
                val fps = when(pos) {
                    0 -> 60; 1 -> 90; 2 -> 120; 3 -> 144
                    else -> 60
                }
                FPSOverlayService.setTargetFPS(fps)
                if (FPSOverlayService.isRunning) {
                    Toast.makeText(this, "✅ Target FPS: $fps", Toast.LENGTH_SHORT).show()
                }
            }
            override fun onNothingSelected(p0: android.widget.AdapterView<*>?) {}
        }

        // ✅ OVERLAY TOGGLE — FIXED CRASH!
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
        btnOverlay.text = if (FPSOverlayService.isRunning) "🔴 MATIKAN FPS MONITOR" else "🟢 TAMPILKAN FPS MONITOR"
        btnOverlay.setBackgroundColor(
            if (FPSOverlayService.isRunning) 0xFFFF5722.toInt() else 0xFFFF9800.toInt()
        )
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
                Toast.makeText(this, "⚠️ Izin ditolak — tidak bisa menampilkan overlay", Toast.LENGTH_LONG).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateOverlayButton()
    }
}
