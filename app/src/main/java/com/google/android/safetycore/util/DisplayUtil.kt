package com.google.android.safetycore.util
import android.content.Context
import android.view.WindowManager
import android.os.Build

object DisplayUtil {
    fun getRefreshRate(context: Context): Float {
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        val display = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            wm.defaultDisplay
        } else {
            @Suppress("DEPRECATION") wm.defaultDisplay
        }
        return display.mode.refreshRate
    }
}
