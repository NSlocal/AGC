package com.google.android.safetycore

import android.content.Context
import android.graphics.Bitmap
import android.util.Log

object ContentClassifier {
    var isScanningEnabled: Boolean = true
    var nudityWarningEnabled: Boolean = true
    var sensitiveWarningEnabled: Boolean = true
    var blurEnabled: Boolean = true

    enum class ContentType { SAFE, NUDITY, SENSITIVE, UNKNOWN }

    data class ClassificationResult(
        val type: ContentType,
        val confidence: Float,
        val shouldBlock: Boolean,
        val shouldBlur: Boolean
    )

    fun classifyImage(context: Context, bitmap: Bitmap): ClassificationResult {
        if (!SafetyCoreService.isServiceEnabled || !isScanningEnabled) {
            Log.d("ContentClassifier", "Pemindaian dinonaktifkan → SAFE")
            return ClassificationResult(ContentType.SAFE, 1.0f, false, false)
        }
        val randomScore = (0..100).random() / 100f
        return when {
            randomScore > 0.85 && nudityWarningEnabled ->
                ClassificationResult(ContentType.NUDITY, randomScore, true, blurEnabled)
            randomScore > 0.70 && sensitiveWarningEnabled ->
                ClassificationResult(ContentType.SENSITIVE, randomScore, false, blurEnabled)
            else -> ClassificationResult(ContentType.SAFE, 1f - randomScore, false, false)
        }
    }

    fun disableAllFeatures(context: Context) {
        isScanningEnabled = false
        nudityWarningEnabled = false
        sensitiveWarningEnabled = false
        blurEnabled = false
        SafetyCoreService.setEnabled(context, false)
        Log.w("ContentClassifier", "⚠️ SEMUA fitur DINONAKTIFKAN")
    }

    fun enableAllFeatures(context: Context) {
        isScanningEnabled = true
        nudityWarningEnabled = true
        sensitiveWarningEnabled = true
        blurEnabled = true
        SafetyCoreService.setEnabled(context, true)
        Log.i("ContentClassifier", "✅ SEMUA fitur DIAKTIFKAN")
    }
}
