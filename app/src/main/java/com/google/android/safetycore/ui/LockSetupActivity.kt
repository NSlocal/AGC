package com.google.android.safetycore.ui

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.databinding.ActivityLockSetupBinding

class LockSetupActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLockSetupBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLockSetupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSetPin.setOnClickListener {
            val pin = binding.etPin.text.toString()
            val confirm = binding.etConfirmPin.text.toString()

            if (pin.length < 4) {
                Toast.makeText(this, "PIN minimal 4 digit", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (pin != confirm) {
                Toast.makeText(this, "PIN tidak cocok", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            LockManager.setPIN(this, pin)
            Toast.makeText(this, "PIN berhasil disetel", Toast.LENGTH_SHORT).show()
            finish()
        }

        binding.btnRemoveLock.setOnClickListener {
            LockManager.disableLock(this)
            Toast.makeText(this, "Kunci dinonaktifkan", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}
