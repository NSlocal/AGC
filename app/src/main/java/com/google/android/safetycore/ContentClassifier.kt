package com.google.android.safetycore

import android.content.Context
import android.app.ActivityManager
import android.app.Service
import android.content.Intent
import android.os.Build
import android.os.Handler
import android.os.Looper

class ContentClassifier(private val context: Context) {

    // ✅ FUNGSI YANG HILANG — DITAMBAHKAN DISINI
    private fun isServiceEnabled(context: Context, serviceClass: Class<out Service>): Boolean {
        val manager = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val runningServices = manager.getRunningServices(Int.MAX_VALUE)
        val serviceName = serviceClass.name
        for (service in runningServices) {
            if (serviceName == service.service.className) {
                return true
            }
        }
        return false
    }

    // Contoh penggunaan di baris 23 yang error:
    fun checkOverlayServiceStatus(): Boolean {
        // ✅ Sekarang fungsi ada — tidak error lagi!
        return isServiceEnabled(context, FPSOverlayService::class.java)
    }

    // --- SISA KODE FILE ---
    private val handler = Handler(Looper.getMainLooper())
    private var isMonitoring = false

    fun startMonitoring() {
        if (isMonitoring) return
        isMonitoring = true
        // Logika pemantauan konten
    }

    fun stopMonitoring() {
        isMonitoring = false
        handler.removeCallbacksAndMessages(null)
    }

    fun isActive(): Boolean = isMonitoring
}
