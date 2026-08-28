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
    private lateinit var btn: Button
    private val RC_PERM = 1234

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
        btn = findViewById(R.id.btn_toggle_fps)
        updateBtn()
        btn.setOnClickListener { toggle() }
    }

    override fun onResume() {
        super.onResume()
        updateBtn()
    }

    private fun hasPerm(): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(this)
        } else true
    }

    private fun toggle() {
        if (!hasPerm()) {
            val i = Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName"))
            startActivityForResult(i, RC_PERM)
            Toast.makeText(this, "Allow overlay permission", Toast.LENGTH_LONG).show()
            return
        }
        val i = Intent(this, FPSOverlayService::class.java)
        if (FPSOverlayService.isRunning) {
            stopService(i)
            Toast.makeText(this, "Stopped", Toast.LENGTH_SHORT).show()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(i)
            } else {
                startService(i)
            }
            Toast.makeText(this, "FPS Started — Check top-right!", Toast.LENGTH_LONG).show()
        }
        updateBtn()
    }

    private fun updateBtn() {
        btn.text = if (FPSOverlayService.isRunning) "STOP FPS" else "SHOW FPS MONITOR"
    }

    override fun onActivityResult(rq: Int, rs: Int, d: Intent?) {
        super.onActivityResult(rq, rs, d)
        if (rq == RC_PERM && hasPerm()) toggle()
    }
}
