package com.google.android.safetycore.model

data class OverlayConfig(
    val showFps: Boolean = true,
    val showCpu: Boolean = true,
    val showGpu: Boolean = true,
    val showTemp: Boolean = true,
    val showBattery: Boolean = true,
    val textSize: Float = 16f,
    val bgOpacity: Int = 204,
    val positionX: Int = 20,
    val positionY: Int = 80,
    val autoRefresh: Boolean = true,
    val updateIntervalMs: Long = 500
)
