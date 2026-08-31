package com.google.android.safetycore.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import com.google.android.safetycore.manager.GameManager

class PackageReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        val packageName = intent?.data?.schemeSpecificPart ?: return

        when (intent.action) {
            Intent.ACTION_PACKAGE_ADDED, Intent.ACTION_PACKAGE_REPLACED -> {
                // Check if new/installed package matches supported games
                val gameManager = GameManager.getInstance(context)
                if (gameManager.getAllGames().any { it.packageName == packageName }) {
                    // Optionally notify or auto-enable
                }
            }
        }
    }
}
