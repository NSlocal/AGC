package com.google.android.safetycore.ui

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.databinding.ActivityPinVerifyBinding

class PinVerifyActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPinVerifyBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPinVerifyBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVerify.setOnClickListener {
            val pin = binding.etPin.text.toString()
            if (LockManager.verifyPIN(this, pin)) {
                setResult(RESULT_OK)
                finish()
            } else {
                Toast.makeText(this, "PIN salah", Toast.LENGTH_SHORT).show()
                binding.etPin.text.clear()
            }
        }

        binding.btnCancel.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
        }
    }
}
