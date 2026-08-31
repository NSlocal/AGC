package com.google.android.safetycore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.BuildConfig
import com.google.android.safetycore.databinding.ActivityAboutBinding

class AboutActivity : AppCompatActivity() {
    private lateinit var binding: ActivityAboutBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAboutBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.tvAppName.text = "SafetyCore Pro"
        binding.tvVersion.text = "Version ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        binding.tvDesc.text = "Game Performance Toolkit — FPS Monitor & Game Booster\nNo Root Required"
        binding.tvCredits.text = "Built with ❤️ by NSlocal\nGitHub: github.com/NSlocal/AGC"
        binding.btnBack.setOnClickListener { finish() }
    }
}
