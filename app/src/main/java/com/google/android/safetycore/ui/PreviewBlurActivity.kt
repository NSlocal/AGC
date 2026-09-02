package com.google.android.safetycore.ui

import android.os.Bundle
import android.widget.SeekBar
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.databinding.ActivityPreviewBlurBinding

class PreviewBlurActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPreviewBlurBinding
    private var blurAlpha = 0.85f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPreviewBlurBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.blurOverlay.alpha = blurAlpha

        binding.seekBlurAlpha.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seek: SeekBar?, progress: Int, fromUser: Boolean) {
                blurAlpha = progress / 100f
                binding.blurOverlay.alpha = blurAlpha
                binding.tvAlphaValue.text = "${(blurAlpha * 100).toInt()}%"
            }
            override fun onStartTrackingTouch(seek: SeekBar?) {}
            override fun onStopTrackingTouch(seek: SeekBar?) {}
        })

        binding.btnApply.setOnClickListener {
            setResult(RESULT_OK)
            finish()
        }

        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
