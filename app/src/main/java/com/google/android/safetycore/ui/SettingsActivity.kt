package com.google.android.safetycore.ui
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.google.android.safetycore.R
import com.google.android.safetycore.databinding.ActivitySettingsBinding
import com.google.android.safetycore.overlay.FPSOverlayService
import com.google.android.safetycore.service.GameBoostService

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences
    private val RC_OVERLAY = 1001

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = PreferenceManager.getDefaultSharedPreferences(this)
        setupUI()
        updateAllStates()
    }

    private fun setupUI() {
        binding.btnToggleFps.setOnClickListener { toggleFPS() }
        binding.btnToggleBoost.setOnClickListener { toggleBoost() }
        binding.btnSettingsOverlay.setOnClickListener {
            startActivity(Intent(this, FloatingSettingsActivity::class.java))
        }
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
        }
    }

    private fun hasOverlayPerm() = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        Settings.canDrawOverlays(this) else true

    private fun toggleFPS() {
        if (!hasOverlayPerm()) {
            startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")), RC_OVERLAY)
            Toast.makeText(this, "Allow overlay permission", Toast.LENGTH_LONG).show()
            return
        }
        val i = Intent(this, FPSOverlayService::class.java)
        FPSOverlayService.isRunning = !FPSOverlayService.isRunning
        if (FPSOverlayService.isRunning) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) startForegroundService(i)
            else startService(i)
            Toast.makeText(this, "✅ FPS Monitor Started", Toast.LENGTH_SHORT).show()
        } else {
            stopService(i)
            Toast.makeText(this, "⏹ FPS Monitor Stopped", Toast.LENGTH_SHORT).show()
        }
        updateAllStates()
    }

    private fun toggleBoost() {
        val i = Intent(this, GameBoostService::class.java)
        GameBoostService.isRunning = !GameBoostService.isRunning
        if (GameBoostService.isRunning) startService(i) else stopService(i)
        updateAllStates()
    }

    private fun updateAllStates() {
        binding.btnToggleFps.text = if (FPSOverlayService.isRunning) "⏹ STOP FPS" else "🎮 SHOW FPS MONITOR"
        binding.btnToggleBoost.text = if (GameBoostService.isRunning) "🚀 BOOST ACTIVE" else "⚡ START GAME BOOST"
        binding.switchDarkMode.isChecked = prefs.getBoolean("dark_mode", true)
    }

    override fun onActivityResult(rq: Int, rs: Int, d: Intent?) {
        super.onActivityResult(rq, rs, d)
        if (rq == RC_OVERLAY && hasOverlayPerm()) toggleFPS()
    }
}
