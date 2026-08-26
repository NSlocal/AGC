package com.google.android.safetycore.ui
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.R
import com.google.android.safetycore.overlay.FPSOverlayService

class SettingsActivity : AppCompatActivity() {
    private val REQUEST_OVERLAY = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        findViewById<Button>(R.id.btn_overlay).setOnClickListener {
            if (FPSOverlayService.isRunning) {
                FPSOverlayService.stop(this)
                Toast.makeText(this, "Overlay Dimatikan", Toast.LENGTH_SHORT).show()
            } else {
                if (checkPermission()) FPSOverlayService.start(this)
                else requestPermission()
            }
        }
    }
    private fun checkPermission() = if (Build.VERSION.SDK_INT >= 23) Settings.canDrawOverlays(this) else true
    private fun requestPermission() {
        if (Build.VERSION.SDK_INT >= 23) {
            startActivityForResult(Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:$packageName")), REQUEST_OVERLAY)
        }
    }
    override fun onActivityResult(req: Int, res: Int, d: Intent?) {
        super.onActivityResult(req, res, d)
        if (req == REQUEST_OVERLAY && checkPermission()) {
            FPSOverlayService.start(this)
            Toast.makeText(this, "Overlay Dinyalakan", Toast.LENGTH_SHORT).show()
        }
    }
}
