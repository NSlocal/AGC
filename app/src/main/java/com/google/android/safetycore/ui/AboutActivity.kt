package com.google.android.safetycore.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.safetycore.BuildConfig
import com.google.android.safetycore.R
import kotlinx.android.synthetic.main.activity_about.*

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)
        tv_version.text = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"
    }
}
