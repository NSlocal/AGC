package com.google.android.safetycore.content

import android.content.Context
import android.content.SharedPreferences
import com.google.android.safetycore.ui.SettingsActivity
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.Serializable

@Serializable
data class ScanHistoryItem(
    val timestamp: Long,
    val status: ScanStatus,
    val reason: String?,
    val confidence: Float,
    val fileName: String? = null
)

object ScanHistoryManager {
    private const val PREFS_NAME = "SafetyCoreHistory"
    private const val KEY_HISTORY = "scan_history"
    private const val MAX_ITEMS = 100

    fun addItem(context: Context, item: ScanHistoryItem) {
        if (!SettingsActivity.isHistoryEnabled(context)) return
        val history = getHistory(context).toMutableList()
        history.add(0, item)
        if (history.size > MAX_ITEMS) history.removeAt(history.lastIndex)
        saveHistory(context, history)
    }

    fun getHistory(context: Context): List<ScanHistoryItem> {
        val prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val json = prefs.getString(KEY_HISTORY, "[]") ?: "[]"
        return try { Json.decodeFromString(json) } catch (e: Exception) { emptyList() }
    }

    fun clearHistory(context: Context) {
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .remove(KEY_HISTORY).apply()
    }

    private fun saveHistory(context: Context, list: List<ScanHistoryItem>) {
        val json = Json.encodeToString(list)
        context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE).edit()
            .putString(KEY_HISTORY, json).apply()
    }
}
