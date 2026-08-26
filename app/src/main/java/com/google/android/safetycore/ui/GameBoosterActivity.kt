package com.google.android.safetycore.ui

import android.os.Bundle
import android.widget.Switch
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.R
import com.google.android.safetycore.game.GameBooster
import com.google.android.safetycore.game.GameList

class GameBoosterActivity : AppCompatActivity() {

    private lateinit var gameBooster: GameBooster

    private lateinit var switchGameBoosterMaster: Switch
    private lateinit var switchFPSUnlock: Switch
    private lateinit var switchAntiLag: Switch
    private lateinit var switchThermalBypass: Switch
    private lateinit var switchPerformanceMode: Switch
    private lateinit var tvGameList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_game_booster)

        gameBooster = GameBooster(this)
        initViews()
        loadCurrentState()
        setupListeners()
        updateGameList()
    }

    private fun initViews() {
        switchGameBoosterMaster = findViewById(R.id.switch_gamebooster_master)
        switchFPSUnlock = findViewById(R.id.switch_fps_unlock)
        switchAntiLag = findViewById(R.id.switch_anti_lag)
        switchThermalBypass = findViewById(R.id.switch_thermal_bypass)
        switchPerformanceMode = findViewById(R.id.switch_performance_mode)
        tvGameList = findViewById(R.id.tv_supported_games)
    }

    private fun loadCurrentState() {
        switchGameBoosterMaster.isChecked = gameBooster.isGameBoosterEnabled
        switchFPSUnlock.isChecked = gameBooster.fpsUnlockEnabled
        switchAntiLag.isChecked = gameBooster.antiLagEnabled
        switchThermalBypass.isChecked = gameBooster.thermalBypassEnabled
        switchPerformanceMode.isChecked = gameBooster.performanceModeEnabled
        setAllGameSwitchesEnabled(switchGameBoosterMaster.isChecked)
    }

    private fun setupListeners() {
        switchGameBoosterMaster.setOnCheckedChangeListener { _, isChecked ->
            gameBooster.isGameBoosterEnabled = isChecked
            setAllGameSwitchesEnabled(isChecked)
        }
        switchFPSUnlock.setOnCheckedChangeListener { _, e -> gameBooster.fpsUnlockEnabled = e }
        switchAntiLag.setOnCheckedChangeListener { _, e -> gameBooster.antiLagEnabled = e }
        switchThermalBypass.setOnCheckedChangeListener { _, e -> gameBooster.thermalBypassEnabled = e }
        switchPerformanceMode.setOnCheckedChangeListener { _, e -> gameBooster.performanceModeEnabled = e }
    }

    private fun setAllGameSwitchesEnabled(enabled: Boolean) {
        switchFPSUnlock.isEnabled = enabled
        switchAntiLag.isEnabled = enabled
        switchThermalBypass.isEnabled = enabled
        switchPerformanceMode.isEnabled = enabled
    }

    private fun updateGameList() {
        val games = GameList.supportedGames.joinToString("\n") {
            "• ${it.name}\n  Paket: ${it.packageName}\n  Max FPS: ${it.maxSupportedFps}"
        }
        tvGameList.text = "🎮 Game Didukung:\n\n$games"
    }
}
