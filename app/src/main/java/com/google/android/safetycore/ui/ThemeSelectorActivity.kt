package com.google.android.safetycore.ui

import android.os.Bundle
import android.widget.RadioGroup
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.databinding.ActivityThemeSelectorBinding

class ThemeSelectorActivity : AppCompatActivity() {
    private lateinit var binding: ActivityThemeSelectorBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityThemeSelectorBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val current = ThemeManager.getCurrentTheme(this)
        when (current) {
            ThemeManager.AppTheme.SYSTEM -> binding.themeGroup.check(R.id.theme_system)
            ThemeManager.AppTheme.LIGHT -> binding.themeGroup.check(R.id.theme_light)
            ThemeManager.AppTheme.DARK -> binding.themeGroup.check(R.id.theme_dark)
            ThemeManager.AppTheme.GREEN -> binding.themeGroup.check(R.id.theme_green)
            ThemeManager.AppTheme.BLUE -> binding.themeGroup.check(R.id.theme_blue)
            else -> {}
        }

        binding.btnApply.setOnClickListener {
            val selectedId = binding.themeGroup.checkedRadioButtonId
            val theme = when (selectedId) {
                R.id.theme_system -> ThemeManager.AppTheme.SYSTEM
                R.id.theme_light -> ThemeManager.AppTheme.LIGHT
                R.id.theme_dark -> ThemeManager.AppTheme.DARK
                R.id.theme_green -> ThemeManager.AppTheme.GREEN
                R.id.theme_blue -> ThemeManager.AppTheme.BLUE
                else -> ThemeManager.AppTheme.SYSTEM
            }
            ThemeManager.applyTheme(this, theme)
            recreate()
            finish()
        }
    }
}
