package com.google.android.safetycore.ui

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.overlay.FPSOverlayService
import com.google.android.safetycore.R

class SettingsActivity : AppCompatActivity() {

    private lateinit var btnToggleFPS: Button
    private val OVERLAY_PERMISSION_CODE = 1001

    // ✅ FULL SUPPORTED GAMES — WITH PACKAGE NAMES
    private val supportedGames = listOf(
        GameApp("QQ Speed / QQ飞车", "com.tencent.tmgp.speedmobile"),
        GameApp("Speed Drifters", "com.garena.game.fctw"),
        GameApp("Mobile Legends: Bang Bang", "com.mobile.legends"),
        GameApp("PUBG Mobile", "com.tencent.ig"),
        GameApp("PUBG Mobile KR", "com.pubg.krmobile"),
        GameApp("Free Fire", "com.dts.freefireth"),
        GameApp("Free Fire MAX", "com.dts.freefiremax"),
        GameApp("Call of Duty Mobile", "com.activision.callofduty.shooter"),
        GameApp("Genshin Impact", "com.miHoYo.GenshinImpact"),
        GameApp("Honor of Kings", "com.tencent.tmgp.sgame"),
        GameApp("Arena of Valor", "com.ngame.allstar.eu"),
        GameApp("League of Legends: Wild Rift", "com.riotgames.leagueoflegends.wildrift"),
        GameApp("Brawl Stars", "com.supercell.brawlstars"),
        GameApp("Clash of Clans", "com.supercell.clashofclans"),
        GameApp("Clash Royale", "com.supercell.clashroyale"),
        GameApp("eFootball 2025", "jp.konami.pesam"),
        GameApp("Asphalt 9", "com.gameloft.android.ANMP.GloftA9HM"),
        GameApp("All Other Games", "*")
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        btnToggleFPS = findViewById(R.id.btn_toggle_fps)
        updateButton()

        btnToggleFPS.setOnClickListener {
            if (checkPermission()) {
                toggleService()
            } else {
                requestPermission()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        updateButton()
    }

    private fun checkPermission(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else true
    }

    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:$packageName")
            )
            startActivityForResult(intent, OVERLAY_PERMISSION_CODE)
        }
    }

    private fun toggleService() {
        val intent = Intent(this, FPSOverlayService::class.java)
        if (FPSOverlayService.isRunning) {
            stopService(intent)
            Toast.makeText(this, "FPS Monitor Stopped", Toast.LENGTH_SHORT).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intent)
            } else {
                startService(intent)
            }
            Toast.makeText(this, "FPS Monitor Started — Drag to move!", Toast.LENGTH_SHORT).show()
        }
        updateButton()
    }

    private fun updateButton() {
        btnToggleFPS.text = if (FPSOverlayService.isRunning) {
            "STOP FPS MONITOR"
        } else {
            "SHOW FPS MONITOR"
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == OVERLAY_PERMISSION_CODE && checkPermission()) {
            toggleService()
        }
    }

    data class GameApp(val name: String, val packageName: String)
}
