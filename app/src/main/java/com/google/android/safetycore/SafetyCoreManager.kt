package com.google.android.safetycore

import android.content.Context

class SafetyCoreManager(private val context: Context) {

    fun setGlobalEnabled(enabled: Boolean): Boolean {
        SafetyCoreService.setEnabled(context, enabled)
        return true
    }

    fun isGlobalEnabled(): Boolean {
        return SafetyCoreService.isEnabled(context)
    }
}
