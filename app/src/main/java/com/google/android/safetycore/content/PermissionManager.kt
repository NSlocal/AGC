package com.google.android.safetycore.content

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import com.google.android.safetycore.ui.SettingsActivity

object PermissionManager {
    fun hasOverlayPermission(context: Context): Boolean {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            Settings.canDrawOverlays(context)
        } else true
    }

    fun requestOverlayPermission(activity: android.app.Activity, requestCode: Int) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val intent = Intent(
                Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                Uri.parse("package:${activity.packageName}")
            )
            activity.startActivityForResult(intent, requestCode)
        }
    }

    fun showPermissionExplanation(context: Context) {
        Toast.makeText(
            context,
            "Izin tampil di atas aplikasi lain diperlukan untuk fitur FPS Monitor & Blur Overlay",
            Toast.LENGTH_LONG
        ).show()
    }
}
