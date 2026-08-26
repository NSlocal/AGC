package com.google.android.safetycore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.databinding.ActivityGameBoosterBinding

class GameBoosterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityGameBoosterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameBoosterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set default
        binding.switchGameboosterMaster.isChecked = false
        binding.switchFpsUnlock.isChecked = false
        binding.switchAntiLag.isChecked = false
        binding.switchThermalBypass.isChecked = false
        binding.switchPerformanceMode.isChecked = false
    }
}
