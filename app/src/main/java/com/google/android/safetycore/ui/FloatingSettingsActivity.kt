package com.google.android.safetycore.ui
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import com.google.android.safetycore.R

class FloatingSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floating_settings)
        supportFragmentManager.beginTransaction()
            .replace(R.id.settings_container, SettingsFragment()).commit()
    }
    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.overlay_preferences, rootKey)
        }
    }
}
