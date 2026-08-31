package com.google.android.safetycore.ui

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.app.AppCompatDelegate
import com.google.android.safetycore.databinding.ActivitySettingsBinding
import com.google.android.safetycore.util.ThemeUtil

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = getSharedPreferences("app_prefs", MODE_PRIVATE)
        setupUI()
    }

    private fun setupUI() {
        // FPS Overlay Toggle
        binding.btnToggleFps.setOnClickListener {
            val intent = Intent(this, FloatingSettingsActivity::class.java)
            startActivity(intent)
        }

        // Game Booster Toggle
        binding.btnToggleBoost.setOnClickListener {
            // Toggle game booster logic here
        }

        // Overlay Settings
        binding.btnSettingsOverlay.setOnClickListener {
            startActivity(Intent(this, FloatingSettingsActivity::class.java))
        }

        // Benchmark
        binding.btnBenchmark.setOnClickListener {
            startActivity(Intent(this, BenchmarkActivity::class.java))
        }

        // Games List
        binding.btnGames.setOnClickListener {
            startActivity(Intent(this, GameListActivity::class.java))
        }

        // About
        binding.btnAbout.setOnClickListener {
            startActivity(Intent(this, AboutActivity::class.java))
        }

        // Dark Mode
        binding.switchDarkMode.isChecked = ThemeUtil.isDarkMode(this)
        binding.switchDarkMode.setOnCheckedChangeListener { _, isChecked ->
            prefs.edit().putBoolean("dark_mode", isChecked).apply()
            AppCompatDelegate.setDefaultNightMode(
                if (isChecked) AppCompatDelegate.MODE_NIGHT_YES
                else AppCompatDelegate.MODE_NIGHT_NO
            )
            recreate()
        }

        // Back Button
        binding.btnBack.setOnClickListener { finish() }
    }
}
