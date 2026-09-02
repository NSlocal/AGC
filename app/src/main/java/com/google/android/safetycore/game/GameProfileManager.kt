package com.google.android.safetycore.game

import android.content.Context
import android.content.SharedPreferences
import com.google.android.safetycore.ui.SettingsActivity

data class GameProfile(
    val packageName: String,
    val gameName: String,
    val targetFps: Int,
    val antiLagEnabled: Boolean,
    val boostEnabled: Boolean,
    val customSettings: Map<String, Any> = emptyMap()
)

object GameProfileManager {
    private const val PREFS = "GameProfiles"

    private val DEFAULT_PROFILES = listOf(
        GameProfile(
            "com.tencent.tmgp.speedmobile",
            "QQ飞车",
            120,
            antiLagEnabled = true,
            boostEnabled = true
        ),
        GameProfile(
            "com.garena.game.fctw",
            "Speed Drifters",
            120,
            antiLagEnabled = true,
            boostEnabled = true
        )
    )

    fun getProfile(context: Context, packageName: String): GameProfile? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val targetFps = prefs.getInt("${packageName}_fps", 120)
        val antiLag = prefs.getBoolean("${packageName}_antilag", true)
        val boost = prefs.getBoolean("${packageName}_boost", true)
        
        val default = DEFAULT_PROFILES.find { it.packageName == packageName }
            ?: return null
        
        return default.copy(
            targetFps = targetFps,
            antiLagEnabled = antiLag,
            boostEnabled = boost
        )
    }

    fun saveProfile(context: Context, profile: GameProfile) {
        val editor = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
        editor.putInt("${profile.packageName}_fps", profile.targetFps)
        editor.putBoolean("${profile.packageName}_antilag", profile.antiLagEnabled)
        editor.putBoolean("${profile.packageName}_boost", profile.boostEnabled)
        editor.apply()
    }

    fun getAllSupportedGames(): List<GameProfile> = DEFAULT_PROFILES

    fun applyProfile(context: Context, profile: GameProfile) {
        if (!SettingsActivity.isGameBoostEnabled(context)) return
        
        FpsUnlocker.setMaxFps(context, profile.targetFps)
        
        if (profile.antiLagEnabled) {
            AntiLag.start(context)
        } else {
            AntiLag.stop()
        }
    }
}
