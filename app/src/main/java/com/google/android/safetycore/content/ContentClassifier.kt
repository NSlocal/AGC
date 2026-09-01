package com.google.android.safetycore.content
import android.graphics.Bitmap
object ContentClassifier {
    enum class ContentType { NUDITY, SENSITIVE, SAFE, UNKNOWN }
    fun classify(bitmap: Bitmap): ScanResult {
        val type = detectContentType(bitmap)
        return when (type) {
            ContentType.SAFE -> ScanResult(ScanStatus.ALLOWED, 0.95f, "Konten aman")
            ContentType.NUDITY -> ScanResult(ScanStatus.BLOCKED, 0.88f, "Konten ketelanjangan", listOf("Nudity"))
            ContentType.SENSITIVE -> ScanResult(ScanStatus.WARNING, 0.75f, "Konten sensitif", listOf("Sensitive"))
            ContentType.UNKNOWN -> ScanResult(ScanStatus.UNCLASSIFIED, 0f, "Tidak dapat diklasifikasikan")
        }
    }
    private fun detectContentType(bitmap: Bitmap): ContentType = ContentType.SAFE
}
