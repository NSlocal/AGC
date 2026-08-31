package com.google.android.safetycore.ui

import android.content.Intent
import android.os.Build
import android.service.quicksettings.Tile
import android.service.quicksettings.TileService
import androidx.annotation.RequiresApi
import com.google.android.safetycore.overlay.FPSOverlayService

@RequiresApi(Build.VERSION_CODES.N)
class QuickSettingsTileService : TileService() {
    override fun onStartListening() {
        super.onStartListening()
        updateTile()
    }

    override fun onClick() {
        super.onClick()
        val i = Intent(this, FPSOverlayService::class.java)
        FPSOverlayService.isRunning = !FPSOverlayService.isRunning
        if (FPSOverlayService.isRunning) {
            startService(i)
        } else {
            stopService(i)
        }
        updateTile()
    }

    private fun updateTile() {
        val tile = qsTile ?: return
        tile.state = if (FPSOverlayService.isRunning) Tile.STATE_ACTIVE else Tile.STATE_INACTIVE
        tile.label = if (FPSOverlayService.isRunning) "FPS ON" else "FPS OFF"
        tile.updateTile()
    }
}
