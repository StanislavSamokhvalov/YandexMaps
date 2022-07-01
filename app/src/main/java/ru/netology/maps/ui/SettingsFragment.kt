package ru.netology.maps.ui

import android.os.Bundle
import androidx.preference.PreferenceFragmentCompat
import ru.netology.maps.R

class MySettingsFragment : PreferenceFragmentCompat() {
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.preferences, rootKey)
    }
}