package com.google.android.safetycore.util

import android.content.Context
import android.os.Environment
import android.widget.Toast
import com.google.android.safetycore.overlay.FPSOverlayService
import java.io.File
import java.io.FileWriter
import java.text.SimpleDateFormat
import java.util.*

object ExportUtil {
    fun exportFpsLog(context: Context, fpsHistory: List<Int>): Boolean {
        try {
            val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
            val fileName = "SafetyCore_FPS_Log_$timestamp.csv"
            val dir = File(context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS), "SafetyCore")
            if (!dir.exists()) dir.mkdirs()
            val file = File(dir, fileName)
            val writer = FileWriter(file)
            writer.append("Time,FPS,CPU,GPU,Temp,Battery\n")
            fpsHistory.forEachIndexed { i, fps ->
                writer.append("$i,$fps,,,\n")
            }
            writer.flush()
            writer.close()
            Toast.makeText(context, "✅ Saved: ${file.absolutePath}", Toast.LENGTH_LONG).show()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "❌ Export Failed", Toast.LENGTH_SHORT).show()
            return false
        }
    }
}
