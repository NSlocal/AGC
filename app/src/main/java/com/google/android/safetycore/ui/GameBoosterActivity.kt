package com.google.android.safetycore.ui

import android.os.Bundle
import android.widget.Switch
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.R

class GameBoosterActivity : AppCompatActivity() {

    private lateinit var switchMaster: Switch
    private lateinit var switchFPS: Switch
    private lateinit var switchAntiLag: Switch
    private lateinit var switchThermal: Switch
    private lateinit var switchPerf: Switch

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_booster)

        switchMaster = findViewById(R.id.switch_gamebooster_master)
        switchFPS = findViewById(R.id.switch_fps_unlock)
        switchAntiLag = findViewById(R.id.switch_anti_lag)
        switchThermal = findViewById(R.id.switch_thermal_bypass)
        switchPerf = findViewById(R.id.switch_performance_mode)

        // Default semua ON
        switchMaster.isChecked = true
        switchFPS.isChecked = true
        switchAntiLag.isChecked = true
        switchThermal.isChecked = true
        switchPerf.isChecked = true
    }
}
