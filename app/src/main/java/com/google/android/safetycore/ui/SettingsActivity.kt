package com.google.android.safetycore.ui
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.overlay.BlurOverlayService
import com.google.android.safetycore.overlay.FPSOverlayService
import com.google.android.safetycore.databinding.ActivitySettingsBinding

class SettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivitySettingsBinding
    private lateinit var prefs: SharedPreferences
    companion object {
        private const val PREFS = "SafetyCorePrefs"
        const val KEY_SCAN = "scan_enabled"
        const val KEY_BLUR_AUTO = "auto_blur_enabled"
        const val KEY_BLOCK = "block_sensitive"
        const val KEY_FPS = "fps_overlay_enabled"
        const val KEY_BLUR_OVERLAY = "blur_overlay_enabled"
        const val KEY_HISTORY = "history_enabled"
        const val KEY_GAME_BOOST = "game_boost_enabled"

        fun isScanEnabled(ctx: Context) = ctx.getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(KEY_SCAN, true)
        fun isAutoBlurEnabled(ctx: Context) = ctx.getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(KEY_BLUR_AUTO, true)
        fun isBlockEnabled(ctx: Context) = ctx.getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(KEY_BLOCK, true)
        fun isFPSOverlayEnabled(ctx: Context) = ctx.getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(KEY_FPS, false)
        fun isBlurOverlayEnabled(ctx: Context) = ctx.getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(KEY_BLUR_OVERLAY, false)
        fun isHistoryEnabled(ctx: Context) = ctx.getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(KEY_HISTORY, true)
        fun isGameBoostEnabled(ctx: Context) = ctx.getSharedPreferences(PREFS, MODE_PRIVATE).getBoolean(KEY_GAME_BOOST, false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        prefs = getSharedPreferences(PREFS, MODE_PRIVATE)

        binding.scanEnabled.isChecked = isScanEnabled(this)
        binding.autoBlurEnabled.isChecked = isAutoBlurEnabled(this)
        binding.blockSensitive.isChecked = isBlockEnabled(this)
        binding.fpsOverlayEnabled.isChecked = isFPSOverlayEnabled(this)
        binding.blurOverlayEnabled.isChecked = isBlurOverlayEnabled(this)

        binding.scanEnabled.setOnCheckedChangeListener { _, v -> prefs.edit().putBoolean(KEY_SCAN, v).apply() }
        binding.autoBlurEnabled.setOnCheckedChangeListener { _, v -> prefs.edit().putBoolean(KEY_BLUR_AUTO, v).apply() }
        binding.blockSensitive.setOnCheckedChangeListener { _, v -> prefs.edit().putBoolean(KEY_BLOCK, v).apply() }
        binding.fpsOverlayEnabled.setOnCheckedChangeListener { _, v ->
            prefs.edit().putBoolean(KEY_FPS, v).apply()
            if (v) startService(Intent(this, FPSOverlayService::class.java))
            else stopService(Intent(this, FPSOverlayService::class.java))
        }
        binding.blurOverlayEnabled.setOnCheckedChangeListener { _, v ->
            prefs.edit().putBoolean(KEY_BLUR_OVERLAY, v).apply()
            if (v) startService(Intent(this, BlurOverlayService::class.java))
            else stopService(Intent(this, BlurOverlayService::class.java))
        }
        binding.btnStopAll?.setOnClickListener {
            prefs.edit().clear().apply()
            stopService(Intent(this, FPSOverlayService::class.java))
            stopService(Intent(this, BlurOverlayService::class.java))
            finish()
            Toast.makeText(this, "Semua fitur dinonaktifkan", Toast.LENGTH_LONG).show()
        }
    }
}
