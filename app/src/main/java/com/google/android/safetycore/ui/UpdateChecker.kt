package com.google.android.safetycore.ui

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.widget.Toast
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URL

object UpdateChecker {
    private const val UPDATE_URL = "https://api.github.com/repos/NSlocal/AGC/releases/latest"
    private const val PREFS = "SafetyCorePrefs"
    private const val KEY_LAST_CHECK = "last_update_check"
    private const val KEY_SKIP_VERSION = "skip_version"

    data class UpdateInfo(
        val versionName: String,
        val versionCode: Int,
        val downloadUrl: String,
        val notes: String,
        val isPrerelease: Boolean
    )

    suspend fun checkForUpdate(context: Context, force: Boolean = false): UpdateInfo? {
        val prefs = context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
        val now = System.currentTimeMillis()
        val lastCheck = prefs.getLong(KEY_LAST_CHECK, 0)
        
        if (!force && (now - lastCheck) < 12 * 3600 * 1000) return null
        
        return try {
            withContext(Dispatchers.IO) {
                val response = URL(UPDATE_URL).readText()
                val json = JSONObject(response)
                val tagName = json.getString("tag_name")
                val versionName = tagName.removePrefix("v")
                val versionCode = versionName.replace(".", "").toIntOrNull() ?: 100
                val skipVersion = prefs.getString(KEY_SKIP_VERSION, null)
                
                if (skipVersion == versionName) return@withContext null
                
                val notes = json.optString("body", "")
                val isPrerelease = json.optBoolean("prerelease", false)
                val assetArray = json.optJSONArray("assets")
                val downloadUrl = if (assetArray != null && assetArray.length() > 0) {
                    assetArray.optJSONObject(0)?.optString("browser_download_url", "") ?: ""
                } else ""

                UpdateInfo(versionName, versionCode, downloadUrl, notes, isPrerelease)
            }
        } catch (e: Exception) {
            null
        }
    }

    fun openDownloadPage(context: Context) {
        val url = "https://github.com/NSlocal/AGC/releases"
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }

    fun skipVersion(context: Context, version: String) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putString(KEY_SKIP_VERSION, version).apply()
    }

    fun markChecked(context: Context) {
        context.getSharedPreferences(PREFS, Context.MODE_PRIVATE)
            .edit().putLong(KEY_LAST_CHECK, System.currentTimeMillis()).apply()
    }
}
