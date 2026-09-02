package com.google.android.safetycore.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.game.GameProfile
import com.google.android.safetycore.game.GameProfileManager
import com.google.android.safetycore.databinding.ActivityGameSettingsBinding

class GameSettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityGameSettingsBinding
    private var selectedPackage: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityGameSettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        selectedPackage = intent.getStringExtra("package_name")
        val gameName = intent.getStringExtra("game_name") ?: "Game"
        
        binding.tvGameTitle.text = "⚙️ Pengaturan $gameName"
        
        loadProfile(selectedPackage)

        binding.fpsSlider.addOnChangeListener { _, value, _ ->
            binding.tvFpsValue.text = "${value.toInt()} FPS"
        }

        binding.btnSave.setOnClickListener {
            saveProfile()
            Toast.makeText(this, "Pengaturan tersimpan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun loadProfile(packageName: String?) {
        packageName ?: return
        val profile = GameProfileManager.getProfile(this, packageName) ?: return

        binding.fpsSlider.value = profile.targetFps.toFloat()
        binding.tvFpsValue.text = "${profile.targetFps} FPS"
        binding.switchAntiLag.isChecked = profile.antiLagEnabled
        binding.switchBoost.isChecked = profile.boostEnabled
    }

    private fun saveProfile() {
        selectedPackage ?: return
        val profile = GameProfile(
            packageName = selectedPackage!!,
            gameName = intent.getStringExtra("game_name") ?: "",
            targetFps = binding.fpsSlider.value.toInt(),
            antiLagEnabled = binding.switchAntiLag.isChecked,
            boostEnabled = binding.switchBoost.isChecked
        )
        GameProfileManager.saveProfile(this, profile)
    }
}
