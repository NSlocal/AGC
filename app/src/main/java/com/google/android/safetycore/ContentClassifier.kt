package com.google.android.safetycore

import android.content.Context
import android.app.ActivityManager
import android.os.Handler
import android.os.Looper

class ContentClassifier(private val context: Context) {

    // ✅ FUNGSI CEK SERVICE — VERSI AMAN, TANPA REFERENSI YANG HILANG
    private fun isServiceEnabled(serviceName: String): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = manager.getRunningServices(Int.MAX_VALUE)
        for (service in runningServices) {
            if (serviceName == service.service.className) {
                return true
            }
        }
        return false
    }

    // ✅ CEK STATUS — TANPA FPSOverlayService yang belum ada
    fun checkOverlayServiceStatus(): Boolean {
        // Sementara false, nanti kalau FPSOverlayService sudah dibuat → ganti
        return isServiceEnabled("com.google.android.safetycore.FPSOverlayService")
    }

    // --- SISA KODE ---
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false

    fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true
    }

    fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacksAndMessages(null)
    }

    fun isActive(): Boolean = isMonitoring
}
