package com.google.android.safetycore.ui

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.databinding.ActivityBackupRestoreBinding
import java.io.*

class BackupRestoreActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBackupRestoreBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBackupRestoreBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnBackup.setOnClickListener { backupSettings() }
        binding.btnRestore.setOnClickListener { restoreSettings() }
    }

    private fun backupSettings() {
        try {
            val prefs = getSharedPreferences("SafetyCorePrefs", Context.MODE_PRIVATE)
            val file = File(getExternalFilesDir(null), "safetycore_backup.txt")
            file.bufferedWriter().use { writer ->
                prefs.all.forEach { (k, v) -> writer.write("$k=${v}\n") }
            }
            Toast.makeText(this, "Backup tersimpan: ${file.absolutePath}", Toast.LENGTH_LONG).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Backup gagal: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }

    private fun restoreSettings() {
        Toast.makeText(this, "Pilih file backup untuk dipulihkan", Toast.LENGTH_SHORT).show()
    }
}
