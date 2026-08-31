package com.google.android.safetycore.model

data class GameInfo(
    val name: String,
    val packageName: String,
    val maxFps: Int,
    val refreshRate: Int,
    val optimizations: List<String>,
    val enabled: Boolean = true
)
