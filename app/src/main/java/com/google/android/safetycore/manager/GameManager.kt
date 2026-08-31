package com.google.android.safetycore.manager

import android.content.Context
import android.content.SharedPreferences
import com.google.android.safetycore.model.GameInfo
import com.google.android.safetycore.util.PreferenceKeys
import org.json.JSONArray
import org.json.JSONObject

class GameManager private constructor(context: Context) {
    private val prefs: SharedPreferences = context.getSharedPreferences("game_prefs", Context.MODE_PRIVATE)

    companion object {
        @Volatile
        private var instance: GameManager? = null

        fun getInstance(context: Context): GameManager {
            return instance ?: synchronized(this) {
                instance ?: GameManager(context.applicationContext).also { instance = it }
            }
        }

        val DEFAULT_GAMES = listOf(
            GameInfo(
                name = "QQ Speed / QQ飞车",
                packageName = "com.tencent.tmgp.speedmobile",
                maxFps = 120,
                refreshRate = 120,
                optimizations = listOf("refresh_rate", "touch_response", "low_latency")
            ),
            GameInfo(
                name = "Speed Drifters",
                packageName = "com.garena.game.fctw",
                maxFps = 120,
                refreshRate = 120,
                optimizations = listOf("refresh_rate", "touch_response", "vsync_align")
            ),
            GameInfo(
                name = "Genshin Impact",
                packageName = "com.miHoYo.GenshinImpact",
                maxFps = 60,
                refreshRate = 60,
                optimizations = listOf("stability", "power_efficiency", "frame_stabilize")
            ),
            GameInfo(
                name = "Wild Rift",
                packageName = "com.riotgames.leagueoflegends.wildrift",
                maxFps = 120,
                refreshRate = 120,
                optimizations = listOf("input_latency", "render_ahead", "pipeline_optimize")
            )
        )
    }

    fun getAllGames(): List<GameInfo> {
        val jsonStr = prefs.getString("games_list", null) ?: return DEFAULT_GAMES
        return try {
            val json = JSONArray(jsonStr)
            List(json.length()) { i ->
                val obj = json.getJSONObject(i)
                GameInfo(
                    name = obj.getString("name"),
                    packageName = obj.getString("packageName"),
                    maxFps = obj.getInt("maxFps"),
                    refreshRate = obj.getInt("refreshRate"),
                    optimizations = obj.optJSONArray("optimizations")?.let { arr ->
                        List(arr.length()) { arr.getString(it) }
                    } ?: emptyList(),
                    enabled = obj.optBoolean("enabled", true)
                )
            }
        } catch (e: Exception) {
            DEFAULT_GAMES
        }
    }

    fun isGameSupported(packageName: String): Boolean {
        return getAllGames().any { it.packageName == packageName && it.enabled }
    }

    fun getGameInfo(packageName: String): GameInfo? {
        return getAllGames().find { it.packageName == packageName }
    }

    fun saveCustomGame(game: GameInfo) {
        val games = getAllGames().toMutableList().apply {
            removeAll { it.packageName == game.packageName }
            add(game)
        }
        val json = JSONArray().apply {
            games.forEach { g ->
                put(JSONObject().apply {
                    put("name", g.name)
                    put("packageName", g.packageName)
                    put("maxFps", g.maxFps)
                    put("refreshRate", g.refreshRate)
                    put("optimizations", JSONArray(g.optimizations))
                    put("enabled", g.enabled)
                })
            }
        }
        prefs.edit().putString("games_list", json.toString()).apply()
    }

    fun setGameEnabled(packageName: String, enabled: Boolean) {
        val games = getAllGames().map {
            if (it.packageName == packageName) it.copy(enabled = enabled) else it
        }
        val json = JSONArray().apply {
            games.forEach { g ->
                put(JSONObject().apply {
                    put("name", g.name)
                    put("packageName", g.packageName)
                    put("maxFps", g.maxFps)
                    put("refreshRate", g.refreshRate)
                    put("optimizations", JSONArray(g.optimizations))
                    put("enabled", g.enabled)
                })
            }
        }
        prefs.edit().putString("games_list", json.toString()).apply()
    }
}
