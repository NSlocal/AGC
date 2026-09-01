package com.google.android.safetycore.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.databinding.ActivityContentWarningBinding

class ContentWarningActivity : AppCompatActivity() {
    private lateinit var binding: ActivityContentWarningBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityContentWarningBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvWarningMessage.text = intent.getStringExtra("reason") ?: "Konten mungkin sensitif"
        binding.btnShowAnyway.setOnClickListener { setResult(RESULT_OK); finish() }
        binding.btnBlock.setOnClickListener { setResult(RESULT_CANCELED); finish() }
    }
}
