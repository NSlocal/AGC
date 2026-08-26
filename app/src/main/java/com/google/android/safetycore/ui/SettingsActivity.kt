package com.google.android.safetycore.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.ContentClassifier
import com.google.android.safetycore.R
import com.google.android.safetycore.SafetyCoreService

class SettingsActivity : AppCompatActivity() {

    private lateinit var switchGlobal: Switch
    private lateinit var switchScan: Switch
    private lateinit var switchNudity: Switch
    private lateinit var switchSensitive: Switch
    private lateinit var switchBlur: Switch
    private lateinit var btnGameBooster: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_safetycore_settings)

        switchGlobal = findViewById(R.id.switch_global_enable)
        switchScan = findViewById(R.id.switch_scan_images)
        switchNudity = findViewById(R.id.switch_nudity_warn)
        switchSensitive = findViewById(R.id.switch_sensitive_warn)
        switchBlur = findViewById(R.id.switch_blur)
        btnGameBooster = findViewById(R.id.btn_go_game_booster)

        loadCurrentState()

        switchGlobal.setOnCheckedChangeListener { _, isChecked ->
            SafetyCoreService.setEnabled(this, isChecked)
            setAllSwitchesEnabled(isChecked)
        }
        switchScan.setOnCheckedChangeListener { _, e -> ContentClassifier.isScanningEnabled = e }
        switchNudity.setOnCheckedChangeListener { _, e -> ContentClassifier.nudityWarningEnabled = e }
        switchSensitive.setOnCheckedChangeListener { _, e -> ContentClassifier.sensitiveWarningEnabled = e }
        switchBlur.setOnCheckedChangeListener { _, e -> ContentClassifier.blurEnabled = e }

        btnGameBooster.setOnClickListener {
            startActivity(Intent(this, GameBoosterActivity::class.java))
        }
    }

    private fun loadCurrentState() {
        switchGlobal.isChecked = SafetyCoreService.isEnabled(this)
        switchScan.isChecked = ContentClassifier.isScanningEnabled
        switchNudity.isChecked = ContentClassifier.nudityWarningEnabled
        switchSensitive.isChecked = ContentClassifier.sensitiveWarningEnabled
        switchBlur.isChecked = ContentClassifier.blurEnabled
        setAllSwitchesEnabled(switchGlobal.isChecked)
    }

    private fun setAllSwitchesEnabled(enabled: Boolean) {
        switchScan.isEnabled = enabled
        switchNudity.isEnabled = enabled
        switchSensitive.isEnabled = enabled
        switchBlur.isEnabled = enabled
    }
}
