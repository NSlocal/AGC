package com.google.android.safetycore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.databinding.ActivityPrivacySettingsBinding

class PrivacySettingsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPrivacySettingsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPrivacySettingsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tvPrivacyInfo.text = """
        🛡️ Kebijakan Privasi SafetyCore
        
        🔒 Data Lokal Semua
        - Pemindaian gambar dilakukan di perangkat
        - TIDAK dikirim ke server kecuali diizinkan
        - Riwayat disimpan di perangkat saja
        
        📱 Izin yang Digunakan
        - SYSTEM_ALERT_WINDOW: untuk overlay FPS & Blur
        - POST_NOTIFICATIONS: notifikasi layanan
        - READ_MEDIA_IMAGES: memindai gambar
        
        ❌ Tidak Mengumpulkan
        - Data pribadi tanpa izin
        - Lokasi, kontak, mikrofon
        - Informasi identitas
        
        Anda dapat mematikan SEMUA fitur kapan saja.
        """.trimIndent()
    }
}
