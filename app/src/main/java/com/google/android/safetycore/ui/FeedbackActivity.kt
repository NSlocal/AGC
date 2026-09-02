package com.google.android.safetycore.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.databinding.ActivityFeedbackBinding

class FeedbackActivity : AppCompatActivity() {
    private lateinit var binding: ActivityFeedbackBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFeedbackBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSendFeedback.setOnClickListener {
            val subject = binding.etSubject.text.toString().trim()
            val message = binding.etMessage.text.toString().trim()

            if (subject.isEmpty() || message.isEmpty()) {
                Toast.makeText(this, "Silakan lengkapi subjek dan pesan", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val emailIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:safetycore@example.com")
                putExtra(Intent.EXTRA_SUBJECT, "SafetyCore Feedback: $subject")
                putExtra(Intent.EXTRA_TEXT, message)
            }

            try {
                startActivity(Intent.createChooser(emailIntent, "Kirim via email..."))
                finish()
            } catch (e: Exception) {
                Toast.makeText(this, "Tidak ada aplikasi email", Toast.LENGTH_SHORT).show()
            }
        }

        binding.btnReportBug.setOnClickListener {
            val url = "https://github.com/NSlocal/AGC/issues/new?labels=bug"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
    }
}
