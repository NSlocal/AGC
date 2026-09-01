package com.google.android.safetycore
import android.content.Context
import android.content.Intent
import android.net.Uri
import com.google.android.safetycore.content.ContentScannerService
object SafetyCoreManager {
    fun scanImage(context: Context, uri: Uri) {
        val intent = Intent(context, ContentScannerService::class.java)
        intent.putExtra(ContentScannerService.EXTRA_URI, uri)
        context.startForegroundService(intent)
    }
}
