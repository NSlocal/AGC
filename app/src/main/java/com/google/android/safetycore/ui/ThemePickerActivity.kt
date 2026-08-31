package com.google.android.safetycore.ui

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.R
import com.google.android.safetycore.databinding.ActivityThemePickerBinding
import com.google.android.safetycore.util.ThemeUtil

class ThemePickerActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThemePickerBinding
    private val themes = listOf(
        ThemeOption("Dark Gaming", "#121212", "#00FF00", "#000000"),
        ThemeOption("Neon Blue", "#0A192F", "#00D8FF", "#0A192F"),
        ThemeOption("Sunset Orange", "#2B1B00", "#FF9800", "#2B1B00"),
        ThemeOption("Pure Black", "#000000", "#FFFFFF", "#000000"),
        ThemeOption("Light Mode", "#FFFFFF", "#6200EE", "#F5F5F5")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemePickerBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setupUI()
    }

    private fun setupUI() {
        binding.tvTitle.text = "🎨 Overlay Theme"
        binding.btnBack.setOnClickListener { finish() }
        themes.forEachIndexed { index, theme ->
            val radio = RadioButton(this)
            radio.text = theme.name
            radio.setTextColor(Color.WHITE)
            radio.tag = index
            radio.setPadding(32, 24, 32, 24)
            radio.setBackgroundColor(Color.parseColor(theme.bgPreview))
            binding.radioGroup.addView(radio)
        }
        binding.btnApply.setOnClickListener {
            val selectedId = binding.radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selected = binding.radioGroup.findViewById<RadioButton>(selectedId)
                val theme = themes[selected.tag as Int]
                // Save theme preference
                finish()
            }
        }
    }
}

data class ThemeOption(val name: String, val bgPreview: String, val textColor: String, val cardBg: String)
