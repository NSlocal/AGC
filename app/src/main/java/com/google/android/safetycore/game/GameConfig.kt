package com.google.android.safetycore.game

data class GameConfig(
    val packageName: String,
    val name: String,
    val maxSupportedFps: Int,
    val unlockMethod: UnlockMethod,
    val antiLagEnabled: Boolean = true,
    val deviceSpoofRequired: Boolean = true
)

enum class UnlockMethod {
    SPOOF_DEVICE,
    CONFIG_OVERRIDE,
    UNITY_PATCH,
    NATIVE_HOOK
}
