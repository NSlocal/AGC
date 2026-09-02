package com.google.android.safetycore.ui

import android.os.Bundle
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.google.android.safetycore.R

class SettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.main_preferences, rootKey)
    }

    override fun onPreferenceTreeClick(preference: Preference): Boolean {
        when (preference.key) {
            "benchmark" -> {
                // Navigate to benchmark or stats activity - using MainActivity as fallback
                try {
                    startActivity(android.content.Intent(requireContext(), Class.forName("com.google.android.safetycore.ui.BenchmarkActivity")))
                } catch (e: ClassNotFoundException) {
                    // BenchmarkActivity not found, navigate to MainActivity instead
                    startActivity(android.content.Intent(requireContext(), MainActivity::class.java))
                }
                return true
            }
            "themes" -> {
                startActivity(android.content.Intent(requireContext(), ThemePickerActivity::class.java))
                return true
            }
            "games_list" -> {
                startActivity(android.content.Intent(requireContext(), GameListActivity::class.java))
                return true
            }
            "about" -> {
                startActivity(android.content.Intent(requireContext(), AboutActivity::class.java))
                return true
            }
        }
        return super.onPreferenceTreeClick(preference)
    }
}
