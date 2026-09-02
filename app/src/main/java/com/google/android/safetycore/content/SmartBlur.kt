package com.google.android.safetycore.content

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import android.renderscript.Allocation
import android.renderscript.Element
import android.renderscript.RenderScript
import android.renderscript.ScriptIntrinsicBlur
import android.os.Build
import com.google.android.safetycore.ui.SettingsActivity

object SmartBlur {
    private const val BLUR_RADIUS = 25f // 1–25

    fun applyBlur(context: Context, bitmap: Bitmap, radius: Float = BLUR_RADIUS): Bitmap {
        if (!SettingsActivity.isAutoBlurEnabled(context)) return bitmap
        
        val scaledWidth = (bitmap.width * 0.25f).toInt()
        val scaledHeight = (bitmap.height * 0.25f).toInt()
        
        val input = Bitmap.createScaledBitmap(bitmap, scaledWidth, scaledHeight, false)
        val output = Bitmap.createBitmap(scaledWidth, scaledHeight, Bitmap.Config.ARGB_8888)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            try {
                val rs = RenderScript.create(context)
                val script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs))
                val inAlloc = Allocation.createFromBitmap(rs, input)
                val outAlloc = Allocation.createFromBitmap(rs, output)
                
                script.setRadius(radius.coerceIn(1f, 25f))
                script.setInput(inAlloc)
                script.forEach(outAlloc)
                outAlloc.copyTo(output)
                
                rs.destroy()
                return Bitmap.createScaledBitmap(output, bitmap.width, bitmap.height, true)
            } catch (e: Exception) {
                // Fallback ke blur sederhana
            }
        }
        
        return fallbackBlur(bitmap, radius)
    }

    private fun fallbackBlur(bitmap: Bitmap, radius: Float): Bitmap {
        val scaled = Bitmap.createScaledBitmap(bitmap, 
            (bitmap.width * 0.3f).toInt(), 
            (bitmap.height * 0.3f).toInt(), 
            true)
        val result = Bitmap.createScaledBitmap(scaled, bitmap.width, bitmap.height, true)
        scaled.recycle()
        return result
    }

    fun createSensitiveOverlay(context: Context, bitmap: Bitmap, regions: List<Rect>): Bitmap {
        val result = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(result)
        val paint = Paint().apply {
            color = 0xCC000000.toInt()
            style = Paint.Style.FILL
        }
        regions.forEach { canvas.drawRect(it, paint) }
        return result
    }
}
