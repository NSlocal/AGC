package com.google.android.safetycore.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.R
import com.google.android.safetycore.SafetyCoreService
import com.google.android.safetycore.databinding.ActivitySafetycoreSettingsBinding

class SettingsActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySafetycoreSettingsBinding
    private val REQUEST_OVERLAY = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySafetycoreSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // ✅ Cek & Minta Izin — TANPA INI APP CRASH!
        checkPermissions()

        // ✅ Init Switch
        binding.switchGlobalEnable.setOnCheckedChangeListener { _, isChecked ->
            SafetyCoreService.setEnabled(this, isChecked)
            Toast.makeText(this, if (isChecked) "✅ SafetyCore Aktif" else "❌ SafetyCore Dinonaktifkan", Toast.LENGTH_SHORT).show()
        }

        // ✅ Tombol Game Booster
        binding.btnGoGameBooster.setOnClickListener {
            startActivity(Intent(this, GameBoosterActivity::class.java))
        }
    }

    private fun checkPermissions() {
        // Cek izin tampil di atas aplikasi lain
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, REQUEST_OVERLAY)
        }
    }

    override fun onResume() {
        super.onResume()
        binding.switchGlobalEnable.isChecked = SafetyCoreService.isEnabled(this)
    }
}
