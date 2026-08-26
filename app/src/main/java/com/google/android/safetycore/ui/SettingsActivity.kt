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

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchGlobal: Switch
    private lateinit var btnGameBooster: Button
    private val REQUEST_OVERLAY = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        try {
            setContentView(R.layout.activity_safetycore_settings)

            switchGlobal = findViewById(R.id.switch_global_enable)
            btnGameBooster = findViewById(R.id.btn_go_game_booster)

            // Cek & minta izin — TANPA CRASH kalau ditolak
            checkOverlayPermission()

            // Set status awal
            switchGlobal.isChecked = SafetyCoreService.isEnabled(this)

            // Listener
            switchGlobal.setOnCheckedChangeListener { _, isChecked ->
                SafetyCoreService.setEnabled(this, isChecked)
                Toast.makeText(this, if (isChecked) "✅ SafetyCore Aktif" else "❌ Dinonaktifkan", Toast.LENGTH_SHORT).show()
            }

            btnGameBooster.setOnClickListener {
                startActivity(Intent(this, GameBoosterActivity::class.java))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, "⚠️ Error: ${e.message}", Toast.LENGTH_LONG).show()
        }
    }

    private fun checkOverlayPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {
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
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (Settings.canDrawOverlays(this)) {
                    Toast.makeText(this, "✅ Izin diberikan!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "⚠️ Izin ditolak — beberapa fitur terbatas", Toast.LENGTH_LONG).show()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::switchGlobal.isInitialized) {
            switchGlobal.isChecked = SafetyCoreService.isEnabled(this)
        }
    }
}
