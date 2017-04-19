package com.example.daniel.assignment2.MyCountries;

import android.os.Bundle;
import android.preference.PreferenceFragment;

import com.example.daniel.assignment2.R;

public class SimplePreferenceFragment extends PreferenceFragment {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.pref_general);
    }
}
