package com.google.android.safetycore.ui

import android.content.Context
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatDelegate

object ThemeManager {
    private const val PREFS = "SafetyCorePrefs"
    private const val KEY_THEME = "app_theme"

    enum class AppTheme(val id: String, val displayName: String, val styleRes: Int) {
        SYSTEM("system", "Ikuti Sistem", -1),
        LIGHT("light", "Terang", AppCompatDelegate.MODE_NIGHT_NO),
        DARK("dark", "Gelap", AppCompatDelegate.MODE_NIGHT_YES),
        AMBER("amber", "Kuning", -1),
        GREEN("green", "Hijau", -1),
        BLUE("blue", "Biru", -1)
    }

    fun getCurrentTheme(context: Context): AppTheme {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val id = prefs.getString(KEY_THEME, "system") ?: "system"
        return AppTheme.values().find { it.id == id } ?: AppTheme.SYSTEM
    }

    fun applyTheme(context: Context, theme: AppTheme) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE).edit()
            .putString(KEY_THEME, theme.id).apply()
        when (theme) {
            AppTheme.SYSTEM -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM)
            AppTheme.LIGHT -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO)
            AppTheme.DARK -> AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES)
            else -> {}
        }
    }
}
