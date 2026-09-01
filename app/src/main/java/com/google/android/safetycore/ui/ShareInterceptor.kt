package com.google.android.safetycore.ui

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import com.google.android.safetycore.content.ContentScannerService
import com.google.android.safetycore.content.ScanResult
import com.google.android.safetycore.content.ScanStatus

class ShareInterceptor : BroadcastReceiver() {
    companion object {
        const val TAG = "ShareInterceptor"
        const val ACTION_SHARE_INTERCEPT = "com.google.android.safetycore.SHARE_INTERCEPT"
    }

    override fun onReceive(context: Context?, intent: Intent?) {
        context ?: return
        intent ?: return

        if (!SettingsActivity.isScanEnabled(context)) return

        if (Intent.ACTION_SEND == intent.action || intent.action?.startsWith("android.intent.action.SEND") == true) {
            val uri = intent.getParcelableExtra<Uri>(Intent.EXTRA_STREAM)
            uri?.let {
                Log.d(TAG, "Share detected — scanning...")
                ContentScannerService().onStartCommand(
                    Intent(context, ContentScannerService::class.java)
                        .putExtra(ContentScannerService.EXTRA_URI, it),
                    0, 0
                )
            }
        }
    }
}
