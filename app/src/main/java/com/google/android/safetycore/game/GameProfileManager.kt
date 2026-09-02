package com.google.android.safetycore.game

class GameProfileManager {
    private var gameMonitor: GameProfileManager? = null

    fun start() {
        gameMonitor = GameProfileManager()
    }

    fun stop() {
        gameMonitor = null
    }
}
