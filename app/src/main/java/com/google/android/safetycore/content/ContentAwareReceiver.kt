package com.google.android.safetycore.content

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.safetycore.ui.NotificationHelper
import com.google.android.safetycore.ui.SettingsActivity

class ContentAwareReceiver : BroadcastReceiver() {
    companion object {
        const val TAG = "ContentAwareReceiver"
        const val ACTION_NEW_IMAGE = "com.google.android.safetycore.NEW_IMAGE"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        if (!SettingsActivity.isScanEnabled(context)) return

        when (intent.action) {
            Intent.ACTION_SEND, Intent.ACTION_SEND_MULTIPLE -> {
                handleShareIntent(context, intent)
            }
            ACTION_NEW_IMAGE -> {
                val uri = intent.getParcelableExtra<Uri>("image_uri")
                uri?.let { scanImage(context, it) }
            }
        }
    }

    private fun handleShareIntent(context: Context, intent: Intent) {
        val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
        uri?.let {
            Log.d(TAG, "Share detected — scanning...")
            scanImage(context, it)
        }
    }

    private fun scanImage(context: Context, uri: Uri) {
        val scanIntent = Intent(context, ContentScannerService::class.java).apply {
            putExtra(ContentScannerService.EXTRA_URI, uri)
        }
        context.startForegroundService(scanIntent)
    }
}
