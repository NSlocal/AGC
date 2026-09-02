package com.google.android.safetycore.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.R
import kotlinx.android.synthetic.main.activity_feedback.*

class FeedbackActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feedback)

        btnSendFeedback.setOnClickListener {
            val subj = etSubject.text.toString()
            val msg = etMessage.text.toString()
            if (subj.isNotEmpty() && msg.isNotEmpty()) {
                Toast.makeText(this, "Terima kasih atas masukan Anda!", Toast.LENGTH_SHORT).show()
                etSubject.text.clear()
                etMessage.text.clear()
            } else {
                Toast.makeText(this, "Isi subjek & pesan terlebih dahulu", Toast.LENGTH_SHORT).show()
            }
        }

        btnReportBug.setOnClickListener {
            Toast.makeText(this, "Laporan bug dikirim!", Toast.LENGTH_SHORT).show()
        }
    }
}
