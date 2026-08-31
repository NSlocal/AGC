package com.google.android.safetycore.model
data class GameInfo(
    val name: String,
    val packageName: String,
    val maxFps: Int,
    val optimizations: List<String>
)
