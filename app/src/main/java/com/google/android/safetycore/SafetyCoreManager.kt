package com.google.android.safetycore

import android.content.Context

class SafetyCoreManager(private val ctx: Context) {
    fun setEnabled(enabled: Boolean) = SafetyCoreService.setEnabled(ctx, enabled)
    fun isEnabled() = SafetyCoreService.isEnabled(ctx)
}
