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

        binding.tvAppName.text = "🛡️ SafetyCore"
        binding.tvVersion.text = "Versi ${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
        binding.tvDescription.text = """
        Perlindungan konten & pemantauan sistem
        
        ✅ Pemindaian gambar
        ✅ Blur otomatis konten sensitif
        ✅ FPS Monitor
        ✅ Monitor CPU/GPU/Suhu
        ✅ Game Booster (QQ飞车 / Speed Drifters)
        ✅ Riwayat pemindaian
        ✅ Tema & kunci pengaturan
        ✅ Semua fitur dapat dinonaktifkan
        
        © 2026 SafetyCore — Privasi & Kendali di Tangan Anda
        """.trimIndent()
    }
}
