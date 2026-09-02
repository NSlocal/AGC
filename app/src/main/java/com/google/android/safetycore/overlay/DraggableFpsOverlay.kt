package com.google.android.safetycore.overlay

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout

class DraggableFpsOverlay(context: Context) : FrameLayout(context) {
    init {
        LayoutInflater.from(context).inflate(R.layout.overlay_fps, this, true)
    }
}
