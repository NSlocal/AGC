package com.google.android.safetycore.ui

import android.content.Intent
import android.net.Uri
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safetycore_settings)

        switchGlobal = findViewById(R.id.switch_global_enable)
        btnGameBooster = findViewById(R.id.btn_go_game_booster)

        // Minta izin tampil di atas aplikasi lain
        if (!Settings.canDrawOverlays(this)) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivity(intent)
        }

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
    }

    override fun onResume() {
        super.onResume()
        switchGlobal.isChecked = SafetyCoreService.isEnabled(this)
    }
}
