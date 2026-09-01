package com.google.android.safetycore.content
enum class ScanStatus { ALLOWED, WARNING, BLOCKED, UNCLASSIFIED }
data class ScanResult(
    val status: ScanStatus,
    val confidence: Float = 0f,
    val reason: String? = null,
    val categories: List<String> = emptyList()
)
