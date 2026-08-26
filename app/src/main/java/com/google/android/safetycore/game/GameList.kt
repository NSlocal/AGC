package com.google.android.safetycore.game

object GameList {
    val supportedGames = listOf(
        GameConfig(
            packageName = "com.tencent.tmgp.speedmobile",
            name = "QQ飞车 / Speed Mobile",
            maxSupportedFps = 144,
            unlockMethod = UnlockMethod.SPOOF_DEVICE,
            antiLagEnabled = true,
            deviceSpoofRequired = true
        ),
        GameConfig(
            packageName = "com.garena.game.fctw",
            name = "Speed Drifters",
            maxSupportedFps = 120,
            unlockMethod = UnlockMethod.CONFIG_OVERRIDE,
            antiLagEnabled = true,
            deviceSpoofRequired = true
        )
    )

    fun getConfig(packageName: String): GameConfig? =
        supportedGames.find { it.packageName == packageName }

    fun isSupported(packageName: String): Boolean =
        supportedGames.any { it.packageName == packageName }
}
