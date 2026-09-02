package com.google.android.safetycore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.databinding.ActivityBenchmarkBinding

class BenchmarkActivity : AppCompatActivity() {
    private lateinit var binding: ActivityBenchmarkBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBenchmarkBinding.inflate(layoutInflater)
        setContentView(binding.root)
        
        // Benchmark functionality can be implemented here
    }
}
